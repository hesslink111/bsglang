package io.deltawave.bsg

import kotlin.test.Test
import kotlin.test.assertEquals

class CompilerTest {
    @Test
    fun emptyClass() {
        val inputFiles = mapOf(
                "Empty.bsg" to """
                    class Empty {}
                """.trimIndent()
        )

        val expectedEmptyH = """
            #ifndef BSG_H__Empty
            #define BSG_H__Empty
            
            // Includes
            #include "bsg_preamble.h"
            
            // Type Num
            #define BSG_Type__Empty 0l
            
            // Instance
            struct BSG_Instance__Empty {
            struct BSG_AnyBaseInstance* baseInstance;
            struct BSG_Class__Empty* class;
            };
            typedef struct BSG_Instance__Empty* BSG_InstancePtr__Empty;
            
            // Base Instance
            struct BSG_BaseInstance__Empty {
                struct BSG_AnyBaseClass* baseClass;
                int refCount;
                struct BSG_Instance__Empty Empty;
            };
            
            // Method Typedefs
            
            // Class
            struct BSG_Class__Empty {
            };
            
            // Base Methods
            struct BSG_AnyInstance* BSG_BaseMethod__Empty_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
            BSG_Bool BSG_BaseMethod__Empty_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
            void BSG_BaseMethod__Empty_retain(struct BSG_AnyBaseInstance* base);
            void BSG_BaseMethod__Empty_release(struct BSG_AnyBaseInstance* base);
            
            // Base Class Singleton
            extern struct BSG_BaseClass BSG_BaseClassSingleton__Empty;
            
            // Methods
            
            // Class Singletons
            extern struct BSG_Class__Empty BSG_ClassSingleton__Empty_Empty;
            
            // Constructor
            extern struct BSG_Instance__Empty* BSG_Constructor__Empty();
            
            // Singleton
            
            #endif
        """.trimIndent() + "\n"

        val expectedEmptyC = """
            // Includes
            #include "Empty.h"
            
            // Base Methods
            struct BSG_AnyInstance* BSG_BaseMethod__Empty_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
                struct BSG_BaseInstance__Empty* b = (struct BSG_BaseInstance__Empty*)base;
                switch(type) {
                    case BSG_Type__Empty:
                        return (struct BSG_AnyInstance*)&b->Empty;
                }
                return NULL;
            }
            BSG_Bool BSG_BaseMethod__Empty_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
                struct BSG_BaseInstance__Empty* b = (struct BSG_BaseInstance__Empty*)base;
                switch(type) {
                    case BSG_Type__Empty:
                        return true;
                }
                return false;
            }
            void BSG_BaseMethod__Empty_retain(struct BSG_AnyBaseInstance* base) {
                base->refCount++;
            }
            void BSG_BaseMethod__Empty_release(struct BSG_AnyBaseInstance* base) {
                base->refCount--;
                if(base->refCount <= 0) {
                    struct BSG_BaseInstance__Empty* b = (struct BSG_BaseInstance__Empty*)base;
                    free(base);
                }
            }
            
            // Base Class Singleton
            struct BSG_BaseClass BSG_BaseClassSingleton__Empty = {
                .cast = &BSG_BaseMethod__Empty_cast,
                .canCast = &BSG_BaseMethod__Empty_canCast,
                .retain = &BSG_BaseMethod__Empty_retain,
                .release = &BSG_BaseMethod__Empty_release,
            };
            
            // Methods
            
            // Class Singletons
            struct BSG_Class__Empty BSG_ClassSingleton__Empty_Empty = {
            };
            
            // Constructor
            struct BSG_Instance__Empty* BSG_Constructor__Empty() {
                struct BSG_BaseInstance__Empty* baseInstance = malloc(sizeof(struct BSG_BaseInstance__Empty));
                baseInstance->Empty = (struct BSG_Instance__Empty) {
                    .baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
                    .class = &BSG_ClassSingleton__Empty_Empty,
                };
                baseInstance->baseClass = (struct BSG_AnyBaseClass*) &BSG_BaseClassSingleton__Empty;
                return &baseInstance->Empty;
            }
            
            // Singleton
        """.trimIndent() + "\n"

        assertEquals(expectedEmptyH, Compiler.compile(listOf("Empty.bsg"), inputFiles)["Empty.h"])
        assertEquals(expectedEmptyC, Compiler.compile(listOf("Empty.bsg"), inputFiles)["Empty.c"])
    }

    @Test
    fun singletonClass_generates() {
        val inputFiles = mapOf(
                "Empty.bsg" to """
                     [Singleton]
                     class Empty {
                          [Main]
                          main(): Void {}
                     }
                """.trimIndent()
        )

        val expectedMainH = """
            // Includes
            #include "Empty.h"
        """.trimIndent() + "\n"

        val expectedMainC = """
            // Includes
            #include "main.h"
            
            // Main
            int main() {
                Empty = BSG_Constructor__Empty();
                Empty->baseInstance->baseClass->retain(Empty->baseInstance);
                Empty->baseInstance->baseClass->retain(Empty->baseInstance);
                Empty->class->main((BSG_AnyInstancePtr)Empty);
                Empty->baseInstance->baseClass->release(Empty->baseInstance);
            }
        """.trimIndent() + "\n"

        assertEquals(expectedMainH, Compiler.compile(listOf("Empty.bsg"), inputFiles)["main.h"])
        assertEquals(expectedMainC, Compiler.compile(listOf("Empty.bsg"), inputFiles)["main.c"])
    }

    @Test
    fun declarationAndAssignment() {
        val dThenAInput = mapOf(
                "DThenA.bsg" to """
                    class DeclarationThenAssignment {
                        main(): Void {
                            var i: Int;
                            i = 1;
                        }
                    }
                """.trimIndent()
        )
        val dThenAOutput = Compiler.compile(listOf("DThenA.bsg"), dThenAInput)

        val dAndAInput = mapOf(
                "DAndA.bsg" to """
                    class DeclarationThenAssignment {
                        main(): Void {
                            var i: Int = 1;
                        }
                    }
                """.trimIndent()
        )
        val dAndAOutput = Compiler.compile(listOf("DAndA.bsg"), dAndAInput)

        assertEquals(dThenAOutput["DThenA.h"], dAndAOutput["DAndA.h"])
        assertEquals(dThenAOutput["DThenA.c"], dAndAOutput["DThenA.c"])
    }

    @Test
    fun poly() {
        val polyInput = mapOf(
                "Animal.bsg" to """
                    class Animal {
                        talk(): Int {
                            return 0;
                        }
                    }
                """.trimIndent(),

                "Cat.bsg" to """
                    class Cat {}
                """.trimIndent(),

                "Dog.bsg" to """
                    class Dog {
                        talk(): Int {
                            return 1;
                        }
                    }
                """.trimIndent()
        )
        val polyOutput = Compiler.compile(listOf("Animal.bsg", "Cat.bsg", "Dog.bsg"), polyInput)

        val expectedPolyOutput = mapOf(
                "Animal.h" to """
                    #ifndef BSG_H__Animal
                    #define BSG_H__Animal
                    
                    // Includes
                    #include "bsg_preamble.h"
                    
                    // Type Num
                    #define BSG_Type__Animal 0l
                    
                    // Instance
                    struct BSG_Instance__Animal {
                    struct BSG_AnyBaseInstance* baseInstance;
                    struct BSG_Class__Animal* class;
                    };
                    typedef struct BSG_Instance__Animal* BSG_InstancePtr__Animal;
                    
                    // Base Instance
                    struct BSG_BaseInstance__Animal {
                        struct BSG_AnyBaseClass* baseClass;
                        int refCount;
                        struct BSG_Instance__Animal Animal;
                    };
                    
                    // Method Typedefs
                    #ifndef BSG_MethodDef__｢｣￫BSG_Int
                    #define BSG_MethodDef__｢｣￫BSG_Int
                    typedef BSG_Int (*BSG_Function__｢BSG_AnyInstancePtr｣￫BSG_Int)(BSG_AnyInstancePtr);
                    typedef struct BSG_MethodFatPtr__｢｣￫BSG_Int {
                        BSG_AnyInstancePtr this;
                        BSG_Function__｢BSG_AnyInstancePtr｣￫BSG_Int method;
                    } BSG_MethodFatPtr__｢｣￫BSG_Int;
                    typedef struct BSG_MethodFatPtr__｢｣￫BSG_Int BSG_MethodFatPtr__｢｣￫BSG_Int;
                    #endif
                    
                    // Class
                    struct BSG_Class__Animal {
                        BSG_Function__｢BSG_AnyInstancePtr｣￫BSG_Int talk;
                    };
                    
                    // Base Methods
                    struct BSG_AnyInstance* BSG_BaseMethod__Animal_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
                    BSG_Bool BSG_BaseMethod__Animal_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
                    void BSG_BaseMethod__Animal_retain(struct BSG_AnyBaseInstance* base);
                    void BSG_BaseMethod__Animal_release(struct BSG_AnyBaseInstance* base);
                    
                    // Base Class Singleton
                    extern struct BSG_BaseClass BSG_BaseClassSingleton__Animal;
                    
                    // Methods
                    BSG_Int BSG_Method__Animal·talk(BSG_AnyInstancePtr _tmp_0);
                    
                    // Class Singletons
                    extern struct BSG_Class__Animal BSG_ClassSingleton__Animal_Animal;
                    
                    // Constructor
                    extern struct BSG_Instance__Animal* BSG_Constructor__Animal();
                    
                    // Singleton
                    
                    #endif
                """.trimIndent() + "\n",

                "Animal.c" to """
                    // Includes
                    #include "Animal.h"
                    
                    // Base Methods
                    struct BSG_AnyInstance* BSG_BaseMethod__Animal_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
                        struct BSG_BaseInstance__Animal* b = (struct BSG_BaseInstance__Animal*)base;
                        switch(type) {
                            case BSG_Type__Animal:
                                return (struct BSG_AnyInstance*)&b->Animal;
                        }
                        return NULL;
                    }
                    BSG_Bool BSG_BaseMethod__Animal_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
                        struct BSG_BaseInstance__Animal* b = (struct BSG_BaseInstance__Animal*)base;
                        switch(type) {
                            case BSG_Type__Animal:
                                return true;
                        }
                        return false;
                    }
                    void BSG_BaseMethod__Animal_retain(struct BSG_AnyBaseInstance* base) {
                        base->refCount++;
                    }
                    void BSG_BaseMethod__Animal_release(struct BSG_AnyBaseInstance* base) {
                        base->refCount--;
                        if(base->refCount <= 0) {
                            struct BSG_BaseInstance__Animal* b = (struct BSG_BaseInstance__Animal*)base;
                            free(base);
                        }
                    }
                    
                    // Base Class Singleton
                    struct BSG_BaseClass BSG_BaseClassSingleton__Animal = {
                        .cast = &BSG_BaseMethod__Animal_cast,
                        .canCast = &BSG_BaseMethod__Animal_canCast,
                        .retain = &BSG_BaseMethod__Animal_retain,
                        .release = &BSG_BaseMethod__Animal_release,
                    };
                    
                    // Methods
                    BSG_Int BSG_Method__Animal·talk(BSG_AnyInstancePtr _tmp_0) {
                        BSG_InstancePtr__Animal this = (struct BSG_Instance__Animal*)_tmp_0;
                        BSG_Int _tmp_1 = 0;
                        if(this) {
                            this->baseInstance->baseClass->release(this->baseInstance);
                        }
                        return _tmp_1;
                    }
                    
                    // Class Singletons
                    struct BSG_Class__Animal BSG_ClassSingleton__Animal_Animal = {
                        .talk = &BSG_Method__Animal·talk,
                    };
                    
                    // Constructor
                    struct BSG_Instance__Animal* BSG_Constructor__Animal() {
                        struct BSG_BaseInstance__Animal* baseInstance = malloc(sizeof(struct BSG_BaseInstance__Animal));
                        baseInstance->Animal = (struct BSG_Instance__Animal) {
                            .baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
                            .class = &BSG_ClassSingleton__Animal_Animal,
                        };
                        baseInstance->baseClass = (struct BSG_AnyBaseClass*) &BSG_BaseClassSingleton__Animal;
                        return &baseInstance->Animal;
                    }
                    
                    // Singleton
                """.trimIndent() + "\n",

                "Cat.h" to """
                    #ifndef BSG_H__Cat
                    #define BSG_H__Cat
                    
                    // Includes
                    #include "bsg_preamble.h"
                    
                    // Type Num
                    #define BSG_Type__Cat 1l
                    
                    // Instance
                    struct BSG_Instance__Cat {
                    struct BSG_AnyBaseInstance* baseInstance;
                    struct BSG_Class__Cat* class;
                    };
                    typedef struct BSG_Instance__Cat* BSG_InstancePtr__Cat;
                    
                    // Base Instance
                    struct BSG_BaseInstance__Cat {
                        struct BSG_AnyBaseClass* baseClass;
                        int refCount;
                        struct BSG_Instance__Cat Cat;
                    };
                    
                    // Method Typedefs
                    
                    // Class
                    struct BSG_Class__Cat {
                    };
                    
                    // Base Methods
                    struct BSG_AnyInstance* BSG_BaseMethod__Cat_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
                    BSG_Bool BSG_BaseMethod__Cat_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
                    void BSG_BaseMethod__Cat_retain(struct BSG_AnyBaseInstance* base);
                    void BSG_BaseMethod__Cat_release(struct BSG_AnyBaseInstance* base);
                    
                    // Base Class Singleton
                    extern struct BSG_BaseClass BSG_BaseClassSingleton__Cat;
                    
                    // Methods
                    
                    // Class Singletons
                    extern struct BSG_Class__Cat BSG_ClassSingleton__Cat_Cat;
                    
                    // Constructor
                    extern struct BSG_Instance__Cat* BSG_Constructor__Cat();
                    
                    // Singleton
                    
                    #endif
                """.trimIndent() + "\n",

                "Cat.c" to """
                    // Includes
                    #include "Cat.h"
                    
                    // Base Methods
                    struct BSG_AnyInstance* BSG_BaseMethod__Cat_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
                        struct BSG_BaseInstance__Cat* b = (struct BSG_BaseInstance__Cat*)base;
                        switch(type) {
                            case BSG_Type__Cat:
                                return (struct BSG_AnyInstance*)&b->Cat;
                        }
                        return NULL;
                    }
                    BSG_Bool BSG_BaseMethod__Cat_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
                        struct BSG_BaseInstance__Cat* b = (struct BSG_BaseInstance__Cat*)base;
                        switch(type) {
                            case BSG_Type__Cat:
                                return true;
                        }
                        return false;
                    }
                    void BSG_BaseMethod__Cat_retain(struct BSG_AnyBaseInstance* base) {
                        base->refCount++;
                    }
                    void BSG_BaseMethod__Cat_release(struct BSG_AnyBaseInstance* base) {
                        base->refCount--;
                        if(base->refCount <= 0) {
                            struct BSG_BaseInstance__Cat* b = (struct BSG_BaseInstance__Cat*)base;
                            free(base);
                        }
                    }
                    
                    // Base Class Singleton
                    struct BSG_BaseClass BSG_BaseClassSingleton__Cat = {
                        .cast = &BSG_BaseMethod__Cat_cast,
                        .canCast = &BSG_BaseMethod__Cat_canCast,
                        .retain = &BSG_BaseMethod__Cat_retain,
                        .release = &BSG_BaseMethod__Cat_release,
                    };
                    
                    // Methods
                    
                    // Class Singletons
                    struct BSG_Class__Cat BSG_ClassSingleton__Cat_Cat = {
                    };
                    
                    // Constructor
                    struct BSG_Instance__Cat* BSG_Constructor__Cat() {
                        struct BSG_BaseInstance__Cat* baseInstance = malloc(sizeof(struct BSG_BaseInstance__Cat));
                        baseInstance->Cat = (struct BSG_Instance__Cat) {
                            .baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
                            .class = &BSG_ClassSingleton__Cat_Cat,
                        };
                        baseInstance->baseClass = (struct BSG_AnyBaseClass*) &BSG_BaseClassSingleton__Cat;
                        return &baseInstance->Cat;
                    }
                    
                    // Singleton
                """.trimIndent() + "\n",

                "Dog.h" to """
                    #ifndef BSG_H__Dog
                    #define BSG_H__Dog
                    
                    // Includes
                    #include "bsg_preamble.h"
                    
                    // Type Num
                    #define BSG_Type__Dog 2l
                    
                    // Instance
                    struct BSG_Instance__Dog {
                    struct BSG_AnyBaseInstance* baseInstance;
                    struct BSG_Class__Dog* class;
                    };
                    typedef struct BSG_Instance__Dog* BSG_InstancePtr__Dog;
                    
                    // Base Instance
                    struct BSG_BaseInstance__Dog {
                        struct BSG_AnyBaseClass* baseClass;
                        int refCount;
                        struct BSG_Instance__Dog Dog;
                    };
                    
                    // Method Typedefs
                    #ifndef BSG_MethodDef__｢｣￫BSG_Int
                    #define BSG_MethodDef__｢｣￫BSG_Int
                    typedef BSG_Int (*BSG_Function__｢BSG_AnyInstancePtr｣￫BSG_Int)(BSG_AnyInstancePtr);
                    typedef struct BSG_MethodFatPtr__｢｣￫BSG_Int {
                        BSG_AnyInstancePtr this;
                        BSG_Function__｢BSG_AnyInstancePtr｣￫BSG_Int method;
                    } BSG_MethodFatPtr__｢｣￫BSG_Int;
                    typedef struct BSG_MethodFatPtr__｢｣￫BSG_Int BSG_MethodFatPtr__｢｣￫BSG_Int;
                    #endif
                    
                    // Class
                    struct BSG_Class__Dog {
                        BSG_Function__｢BSG_AnyInstancePtr｣￫BSG_Int talk;
                    };
                    
                    // Base Methods
                    struct BSG_AnyInstance* BSG_BaseMethod__Dog_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
                    BSG_Bool BSG_BaseMethod__Dog_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
                    void BSG_BaseMethod__Dog_retain(struct BSG_AnyBaseInstance* base);
                    void BSG_BaseMethod__Dog_release(struct BSG_AnyBaseInstance* base);
                    
                    // Base Class Singleton
                    extern struct BSG_BaseClass BSG_BaseClassSingleton__Dog;
                    
                    // Methods
                    BSG_Int BSG_Method__Dog·talk(BSG_AnyInstancePtr _tmp_0);
                    
                    // Class Singletons
                    extern struct BSG_Class__Dog BSG_ClassSingleton__Dog_Dog;
                    
                    // Constructor
                    extern struct BSG_Instance__Dog* BSG_Constructor__Dog();
                    
                    // Singleton
                    
                    #endif
                """.trimIndent() + "\n",

                "Dog.c" to """
                    // Includes
                    #include "Dog.h"
                    
                    // Base Methods
                    struct BSG_AnyInstance* BSG_BaseMethod__Dog_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
                        struct BSG_BaseInstance__Dog* b = (struct BSG_BaseInstance__Dog*)base;
                        switch(type) {
                            case BSG_Type__Dog:
                                return (struct BSG_AnyInstance*)&b->Dog;
                        }
                        return NULL;
                    }
                    BSG_Bool BSG_BaseMethod__Dog_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
                        struct BSG_BaseInstance__Dog* b = (struct BSG_BaseInstance__Dog*)base;
                        switch(type) {
                            case BSG_Type__Dog:
                                return true;
                        }
                        return false;
                    }
                    void BSG_BaseMethod__Dog_retain(struct BSG_AnyBaseInstance* base) {
                        base->refCount++;
                    }
                    void BSG_BaseMethod__Dog_release(struct BSG_AnyBaseInstance* base) {
                        base->refCount--;
                        if(base->refCount <= 0) {
                            struct BSG_BaseInstance__Dog* b = (struct BSG_BaseInstance__Dog*)base;
                            free(base);
                        }
                    }
                    
                    // Base Class Singleton
                    struct BSG_BaseClass BSG_BaseClassSingleton__Dog = {
                        .cast = &BSG_BaseMethod__Dog_cast,
                        .canCast = &BSG_BaseMethod__Dog_canCast,
                        .retain = &BSG_BaseMethod__Dog_retain,
                        .release = &BSG_BaseMethod__Dog_release,
                    };
                    
                    // Methods
                    BSG_Int BSG_Method__Dog·talk(BSG_AnyInstancePtr _tmp_0) {
                        BSG_InstancePtr__Dog this = (struct BSG_Instance__Dog*)_tmp_0;
                        BSG_Int _tmp_1 = 1;
                        if(this) {
                            this->baseInstance->baseClass->release(this->baseInstance);
                        }
                        return _tmp_1;
                    }
                    
                    // Class Singletons
                    struct BSG_Class__Dog BSG_ClassSingleton__Dog_Dog = {
                        .talk = &BSG_Method__Dog·talk,
                    };
                    
                    // Constructor
                    struct BSG_Instance__Dog* BSG_Constructor__Dog() {
                        struct BSG_BaseInstance__Dog* baseInstance = malloc(sizeof(struct BSG_BaseInstance__Dog));
                        baseInstance->Dog = (struct BSG_Instance__Dog) {
                            .baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
                            .class = &BSG_ClassSingleton__Dog_Dog,
                        };
                        baseInstance->baseClass = (struct BSG_AnyBaseClass*) &BSG_BaseClassSingleton__Dog;
                        return &baseInstance->Dog;
                    }
                    
                    // Singleton
                """.trimIndent() + "\n"
        )

        assertEquals(expectedPolyOutput["Animal.h"], polyOutput["Animal.h"])
        assertEquals(expectedPolyOutput["Animal.c"], polyOutput["Animal.c"])
        assertEquals(expectedPolyOutput["Cat.h"], polyOutput["Cat.h"])
        assertEquals(expectedPolyOutput["Cat.c"], polyOutput["Cat.c"])
        assertEquals(expectedPolyOutput["Dog.h"], polyOutput["Dog.h"])
        assertEquals(expectedPolyOutput["Dog.c"], polyOutput["Dog.c"])
    }
}