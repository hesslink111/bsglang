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
            BSG_AnyInstance* BSG_BaseMethod__Empty_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
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
            BSG_AnyInstance* BSG_BaseMethod__Empty_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
                struct BSG_BaseInstance__Empty* b = (struct BSG_BaseInstance__Empty*)base;
                switch(type) {
                    case BSG_Type__Empty:
                        return (BSG_AnyInstance*)&b->Empty;
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
                baseInstance->refCount = 0;
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
                Empty->class->main((BSG_AnyInstance*)Empty);
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
                    typedef BSG_Int (*BSG_Function__｢｣￫BSG_Int)(BSG_AnyInstance*);
                    typedef struct BSG_MethodFatPtr__｢｣￫BSG_Int {
                        BSG_AnyInstance* this;
                        BSG_Function__｢｣￫BSG_Int method;
                    } BSG_MethodFatPtr__｢｣￫BSG_Int;
                    typedef struct BSG_MethodFatPtr__｢｣￫BSG_Int BSG_MethodFatPtr__｢｣￫BSG_Int;
                    #endif
                    
                    // Class
                    struct BSG_Class__Animal {
                        BSG_Function__｢｣￫BSG_Int talk;
                    };
                    
                    // Base Methods
                    BSG_AnyInstance* BSG_BaseMethod__Animal_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
                    BSG_Bool BSG_BaseMethod__Animal_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
                    void BSG_BaseMethod__Animal_retain(struct BSG_AnyBaseInstance* base);
                    void BSG_BaseMethod__Animal_release(struct BSG_AnyBaseInstance* base);
                    
                    // Base Class Singleton
                    extern struct BSG_BaseClass BSG_BaseClassSingleton__Animal;
                    
                    // Methods
                    BSG_Int BSG_Method__Animal·talk(BSG_AnyInstance* _tmp_0);
                    
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
                    BSG_AnyInstance* BSG_BaseMethod__Animal_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
                        struct BSG_BaseInstance__Animal* b = (struct BSG_BaseInstance__Animal*)base;
                        switch(type) {
                            case BSG_Type__Animal:
                                return (BSG_AnyInstance*)&b->Animal;
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
                    BSG_Int BSG_Method__Animal·talk(BSG_AnyInstance* _tmp_0) {
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
                        baseInstance->refCount = 0;
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
                    BSG_AnyInstance* BSG_BaseMethod__Cat_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
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
                    BSG_AnyInstance* BSG_BaseMethod__Cat_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
                        struct BSG_BaseInstance__Cat* b = (struct BSG_BaseInstance__Cat*)base;
                        switch(type) {
                            case BSG_Type__Cat:
                                return (BSG_AnyInstance*)&b->Cat;
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
                        baseInstance->refCount = 0;
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
                    typedef BSG_Int (*BSG_Function__｢｣￫BSG_Int)(BSG_AnyInstance*);
                    typedef struct BSG_MethodFatPtr__｢｣￫BSG_Int {
                        BSG_AnyInstance* this;
                        BSG_Function__｢｣￫BSG_Int method;
                    } BSG_MethodFatPtr__｢｣￫BSG_Int;
                    typedef struct BSG_MethodFatPtr__｢｣￫BSG_Int BSG_MethodFatPtr__｢｣￫BSG_Int;
                    #endif
                    
                    // Class
                    struct BSG_Class__Dog {
                        BSG_Function__｢｣￫BSG_Int talk;
                    };
                    
                    // Base Methods
                    BSG_AnyInstance* BSG_BaseMethod__Dog_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
                    BSG_Bool BSG_BaseMethod__Dog_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
                    void BSG_BaseMethod__Dog_retain(struct BSG_AnyBaseInstance* base);
                    void BSG_BaseMethod__Dog_release(struct BSG_AnyBaseInstance* base);
                    
                    // Base Class Singleton
                    extern struct BSG_BaseClass BSG_BaseClassSingleton__Dog;
                    
                    // Methods
                    BSG_Int BSG_Method__Dog·talk(BSG_AnyInstance* _tmp_0);
                    
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
                    BSG_AnyInstance* BSG_BaseMethod__Dog_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
                        struct BSG_BaseInstance__Dog* b = (struct BSG_BaseInstance__Dog*)base;
                        switch(type) {
                            case BSG_Type__Dog:
                                return (BSG_AnyInstance*)&b->Dog;
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
                    BSG_Int BSG_Method__Dog·talk(BSG_AnyInstance* _tmp_0) {
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
                        baseInstance->refCount = 0;
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

    @Test
    fun methodCast() {
        val polyInput = mapOf(
                "MethodCast.bsg" to """
                    class MethodCast {
                        zeroArgNonVoid(): Any {
                            return 0 as Any;
                        }
                        
                        oneArgVoid(a: Any): Void {}
                        
                        oneArgNonVoid(a: Any): Any {}
                        
                        tester(): Void {
                            zeroArgNonVoid as ()->Int;
                            oneArgVoid as (Int)->Void;
                            oneArgNonVoid as (Int)->Int;
                        }
                    }
                """.trimIndent(),
        )
        val polyOutput = Compiler.compile(listOf("MethodCast.bsg"), polyInput)

        val expectedOutput = mapOf(
                "MethodCast.h" to """
                    #ifndef BSG_H__MethodCast
                    #define BSG_H__MethodCast
                    
                    // Includes
                    #include "bsg_preamble.h"
                    
                    // Type Num
                    #define BSG_Type__MethodCast 0l
                    
                    // Instance
                    struct BSG_Instance__MethodCast {
                    struct BSG_AnyBaseInstance* baseInstance;
                    struct BSG_Class__MethodCast* class;
                    };
                    typedef struct BSG_Instance__MethodCast* BSG_InstancePtr__MethodCast;
                    
                    // Base Instance
                    struct BSG_BaseInstance__MethodCast {
                        struct BSG_AnyBaseClass* baseClass;
                        int refCount;
                        struct BSG_Instance__MethodCast MethodCast;
                    };
                    
                    // Method Typedefs
                    #ifndef BSG_MethodDef__｢｣￫BSG_Any
                    #define BSG_MethodDef__｢｣￫BSG_Any
                    typedef BSG_Any (*BSG_Function__｢｣￫BSG_Any)(BSG_AnyInstance*);
                    typedef struct BSG_MethodFatPtr__｢｣￫BSG_Any {
                        BSG_AnyInstance* this;
                        BSG_Function__｢｣￫BSG_Any method;
                    } BSG_MethodFatPtr__｢｣￫BSG_Any;
                    typedef struct BSG_MethodFatPtr__｢｣￫BSG_Any BSG_MethodFatPtr__｢｣￫BSG_Any;
                    #endif
                    #ifndef BSG_MethodDef__｢BSG_Any｣￫BSG_Void
                    #define BSG_MethodDef__｢BSG_Any｣￫BSG_Void
                    typedef BSG_Void (*BSG_Function__｢BSG_Any｣￫BSG_Void)(BSG_AnyInstance*,BSG_Any);
                    typedef struct BSG_MethodFatPtr__｢BSG_Any｣￫BSG_Void {
                        BSG_AnyInstance* this;
                        BSG_Function__｢BSG_Any｣￫BSG_Void method;
                    } BSG_MethodFatPtr__｢BSG_Any｣￫BSG_Void;
                    typedef struct BSG_MethodFatPtr__｢BSG_Any｣￫BSG_Void BSG_MethodFatPtr__｢BSG_Any｣￫BSG_Void;
                    #endif
                    #ifndef BSG_MethodDef__｢BSG_Any｣￫BSG_Any
                    #define BSG_MethodDef__｢BSG_Any｣￫BSG_Any
                    typedef BSG_Any (*BSG_Function__｢BSG_Any｣￫BSG_Any)(BSG_AnyInstance*,BSG_Any);
                    typedef struct BSG_MethodFatPtr__｢BSG_Any｣￫BSG_Any {
                        BSG_AnyInstance* this;
                        BSG_Function__｢BSG_Any｣￫BSG_Any method;
                    } BSG_MethodFatPtr__｢BSG_Any｣￫BSG_Any;
                    typedef struct BSG_MethodFatPtr__｢BSG_Any｣￫BSG_Any BSG_MethodFatPtr__｢BSG_Any｣￫BSG_Any;
                    #endif
                    #ifndef BSG_MethodDef__｢｣￫BSG_Void
                    #define BSG_MethodDef__｢｣￫BSG_Void
                    typedef BSG_Void (*BSG_Function__｢｣￫BSG_Void)(BSG_AnyInstance*);
                    typedef struct BSG_MethodFatPtr__｢｣￫BSG_Void {
                        BSG_AnyInstance* this;
                        BSG_Function__｢｣￫BSG_Void method;
                    } BSG_MethodFatPtr__｢｣￫BSG_Void;
                    typedef struct BSG_MethodFatPtr__｢｣￫BSG_Void BSG_MethodFatPtr__｢｣￫BSG_Void;
                    #endif
                    #ifndef BSG_MethodDef__｢｣￫BSG_Int
                    #define BSG_MethodDef__｢｣￫BSG_Int
                    typedef BSG_Int (*BSG_Function__｢｣￫BSG_Int)(BSG_AnyInstance*);
                    typedef struct BSG_MethodFatPtr__｢｣￫BSG_Int {
                        BSG_AnyInstance* this;
                        BSG_Function__｢｣￫BSG_Int method;
                    } BSG_MethodFatPtr__｢｣￫BSG_Int;
                    typedef struct BSG_MethodFatPtr__｢｣￫BSG_Int BSG_MethodFatPtr__｢｣￫BSG_Int;
                    #endif
                    #ifndef BSG_MethodDef__｢BSG_Int｣￫BSG_Void
                    #define BSG_MethodDef__｢BSG_Int｣￫BSG_Void
                    typedef BSG_Void (*BSG_Function__｢BSG_Int｣￫BSG_Void)(BSG_AnyInstance*,BSG_Int);
                    typedef struct BSG_MethodFatPtr__｢BSG_Int｣￫BSG_Void {
                        BSG_AnyInstance* this;
                        BSG_Function__｢BSG_Int｣￫BSG_Void method;
                    } BSG_MethodFatPtr__｢BSG_Int｣￫BSG_Void;
                    typedef struct BSG_MethodFatPtr__｢BSG_Int｣￫BSG_Void BSG_MethodFatPtr__｢BSG_Int｣￫BSG_Void;
                    #endif
                    #ifndef BSG_MethodDef__｢BSG_Int｣￫BSG_Int
                    #define BSG_MethodDef__｢BSG_Int｣￫BSG_Int
                    typedef BSG_Int (*BSG_Function__｢BSG_Int｣￫BSG_Int)(BSG_AnyInstance*,BSG_Int);
                    typedef struct BSG_MethodFatPtr__｢BSG_Int｣￫BSG_Int {
                        BSG_AnyInstance* this;
                        BSG_Function__｢BSG_Int｣￫BSG_Int method;
                    } BSG_MethodFatPtr__｢BSG_Int｣￫BSG_Int;
                    typedef struct BSG_MethodFatPtr__｢BSG_Int｣￫BSG_Int BSG_MethodFatPtr__｢BSG_Int｣￫BSG_Int;
                    #endif
                    
                    // Class
                    struct BSG_Class__MethodCast {
                        BSG_Function__｢｣￫BSG_Any zeroArgNonVoid;
                        BSG_Function__｢BSG_Any｣￫BSG_Void oneArgVoid;
                        BSG_Function__｢BSG_Any｣￫BSG_Any oneArgNonVoid;
                        BSG_Function__｢｣￫BSG_Void tester;
                    };
                    
                    // Base Methods
                    BSG_AnyInstance* BSG_BaseMethod__MethodCast_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
                    BSG_Bool BSG_BaseMethod__MethodCast_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
                    void BSG_BaseMethod__MethodCast_retain(struct BSG_AnyBaseInstance* base);
                    void BSG_BaseMethod__MethodCast_release(struct BSG_AnyBaseInstance* base);
                    
                    // Base Class Singleton
                    extern struct BSG_BaseClass BSG_BaseClassSingleton__MethodCast;
                    
                    // Methods
                    #ifndef BSG_MethodCastShimDef_H__｢｣￫BSG_Any_castTo_｢｣￫BSG_Int
                    #define BSG_MethodCastShimDef_H__｢｣￫BSG_Any_castTo_｢｣￫BSG_Int
                    BSG_Int BSG_MethodCastShim__｢｣￫BSG_Any_castTo_｢｣￫BSG_Int(BSG_AnyInstance*);
                    #endif
                    #ifndef BSG_MethodCastShimDef_H__｢BSG_Any｣￫BSG_Void_castTo_｢BSG_Int｣￫BSG_Void
                    #define BSG_MethodCastShimDef_H__｢BSG_Any｣￫BSG_Void_castTo_｢BSG_Int｣￫BSG_Void
                    BSG_Void BSG_MethodCastShim__｢BSG_Any｣￫BSG_Void_castTo_｢BSG_Int｣￫BSG_Void(BSG_AnyInstance*,BSG_Int);
                    #endif
                    #ifndef BSG_MethodCastShimDef_H__｢BSG_Any｣￫BSG_Any_castTo_｢BSG_Int｣￫BSG_Int
                    #define BSG_MethodCastShimDef_H__｢BSG_Any｣￫BSG_Any_castTo_｢BSG_Int｣￫BSG_Int
                    BSG_Int BSG_MethodCastShim__｢BSG_Any｣￫BSG_Any_castTo_｢BSG_Int｣￫BSG_Int(BSG_AnyInstance*,BSG_Int);
                    #endif
                    BSG_Any BSG_Method__MethodCast·zeroArgNonVoid(BSG_AnyInstance* _tmp_0);
                    BSG_Void BSG_Method__MethodCast·oneArgVoid(BSG_AnyInstance* _tmp_3,BSG_Any a);
                    BSG_Any BSG_Method__MethodCast·oneArgNonVoid(BSG_AnyInstance* _tmp_4,BSG_Any a);
                    BSG_Void BSG_Method__MethodCast·tester(BSG_AnyInstance* _tmp_5);
                    
                    // Class Singletons
                    extern struct BSG_Class__MethodCast BSG_ClassSingleton__MethodCast_MethodCast;
                    
                    // Constructor
                    extern struct BSG_Instance__MethodCast* BSG_Constructor__MethodCast();
                    
                    // Singleton
                    
                    #endif
                """.trimIndent() + "\n",

                "MethodCast.c" to """
                    // Includes
                    #include "MethodCast.h"
                    
                    // Base Methods
                    BSG_AnyInstance* BSG_BaseMethod__MethodCast_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
                        struct BSG_BaseInstance__MethodCast* b = (struct BSG_BaseInstance__MethodCast*)base;
                        switch(type) {
                            case BSG_Type__MethodCast:
                                return (BSG_AnyInstance*)&b->MethodCast;
                        }
                        return NULL;
                    }
                    BSG_Bool BSG_BaseMethod__MethodCast_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
                        struct BSG_BaseInstance__MethodCast* b = (struct BSG_BaseInstance__MethodCast*)base;
                        switch(type) {
                            case BSG_Type__MethodCast:
                                return true;
                        }
                        return false;
                    }
                    void BSG_BaseMethod__MethodCast_retain(struct BSG_AnyBaseInstance* base) {
                        base->refCount++;
                    }
                    void BSG_BaseMethod__MethodCast_release(struct BSG_AnyBaseInstance* base) {
                        base->refCount--;
                        if(base->refCount <= 0) {
                            struct BSG_BaseInstance__MethodCast* b = (struct BSG_BaseInstance__MethodCast*)base;
                            free(base);
                        }
                    }
                    
                    // Base Class Singleton
                    struct BSG_BaseClass BSG_BaseClassSingleton__MethodCast = {
                        .cast = &BSG_BaseMethod__MethodCast_cast,
                        .canCast = &BSG_BaseMethod__MethodCast_canCast,
                        .retain = &BSG_BaseMethod__MethodCast_retain,
                        .release = &BSG_BaseMethod__MethodCast_release,
                    };
                    
                    // Methods
                    BSG_Any BSG_Method__MethodCast·zeroArgNonVoid(BSG_AnyInstance* _tmp_0) {
                        BSG_InstancePtr__MethodCast this = (struct BSG_Instance__MethodCast*)_tmp_0;
                        BSG_Int _tmp_1 = 0;
                        BSG_Any _tmp_2;
                        _tmp_2.type = BSG_Any_ContentType__Primitive;
                        _tmp_2.content.primitive.IntValue = _tmp_1;
                        if(this) {
                            this->baseInstance->baseClass->release(this->baseInstance);
                        }
                        return _tmp_2;
                    }
                    BSG_Void BSG_Method__MethodCast·oneArgVoid(BSG_AnyInstance* _tmp_3,BSG_Any a) {
                        BSG_InstancePtr__MethodCast this = (struct BSG_Instance__MethodCast*)_tmp_3;
                        if(this) {
                            this->baseInstance->baseClass->release(this->baseInstance);
                        }
                        if(a.type == BSG_Any_ContentType__Instance && a.content.instance) {
                            a.content.instance->baseInstance->baseClass->release(a.content.instance->baseInstance);
                        } else if(a.type == BSG_Any_ContentType__Method && a.content.method.this) {
                            a.content.method.this->baseInstance->baseClass->release(a.content.method.this->baseInstance);
                        }
                        return;
                    }
                    BSG_Any BSG_Method__MethodCast·oneArgNonVoid(BSG_AnyInstance* _tmp_4,BSG_Any a) {
                        BSG_InstancePtr__MethodCast this = (struct BSG_Instance__MethodCast*)_tmp_4;
                        if(this) {
                            this->baseInstance->baseClass->release(this->baseInstance);
                        }
                        if(a.type == BSG_Any_ContentType__Instance && a.content.instance) {
                            a.content.instance->baseInstance->baseClass->release(a.content.instance->baseInstance);
                        } else if(a.type == BSG_Any_ContentType__Method && a.content.method.this) {
                            a.content.method.this->baseInstance->baseClass->release(a.content.method.this->baseInstance);
                        }
                        return;
                    }
                    BSG_Void BSG_Method__MethodCast·tester(BSG_AnyInstance* _tmp_5) {
                        BSG_InstancePtr__MethodCast this = (struct BSG_Instance__MethodCast*)_tmp_5;
                        BSG_MethodFatPtr__｢｣￫BSG_Any _tmp_6;
                        _tmp_6.this = (BSG_AnyInstance*) this;
                        _tmp_6.method = this->class->zeroArgNonVoid;
                        BSG_Instance__BoxedMethod* _tmp_10 = BSG_Constructor__BoxedMethod();
                        _tmp_10->method.this = _tmp_6.this;
                        _tmp_10->method.method = (BSG_AnyMethod)_tmp_6.method;
                        _tmp_6.this->baseInstance->baseClass->retain(_tmp_6.this->baseInstance);
                        BSG_MethodFatPtr__｢｣￫BSG_Int _tmp_7;
                        _tmp_7.this = (BSG_AnyInstance*)_tmp_10;
                        _tmp_7.method = &BSG_MethodCastShim__｢｣￫BSG_Any_castTo_｢｣￫BSG_Int;
                        if(_tmp_7.this) {
                            _tmp_7.this->baseInstance->baseClass->retain(_tmp_7.this->baseInstance);
                        }
                        BSG_MethodFatPtr__｢BSG_Any｣￫BSG_Void _tmp_11;
                        _tmp_11.this = (BSG_AnyInstance*) this;
                        _tmp_11.method = this->class->oneArgVoid;
                        BSG_Instance__BoxedMethod* _tmp_15 = BSG_Constructor__BoxedMethod();
                        _tmp_15->method.this = _tmp_11.this;
                        _tmp_15->method.method = (BSG_AnyMethod)_tmp_11.method;
                        _tmp_11.this->baseInstance->baseClass->retain(_tmp_11.this->baseInstance);
                        BSG_MethodFatPtr__｢BSG_Int｣￫BSG_Void _tmp_12;
                        _tmp_12.this = (BSG_AnyInstance*)_tmp_15;
                        _tmp_12.method = &BSG_MethodCastShim__｢BSG_Any｣￫BSG_Void_castTo_｢BSG_Int｣￫BSG_Void;
                        if(_tmp_12.this) {
                            _tmp_12.this->baseInstance->baseClass->retain(_tmp_12.this->baseInstance);
                        }
                        BSG_MethodFatPtr__｢BSG_Any｣￫BSG_Any _tmp_16;
                        _tmp_16.this = (BSG_AnyInstance*) this;
                        _tmp_16.method = this->class->oneArgNonVoid;
                        BSG_Instance__BoxedMethod* _tmp_22 = BSG_Constructor__BoxedMethod();
                        _tmp_22->method.this = _tmp_16.this;
                        _tmp_22->method.method = (BSG_AnyMethod)_tmp_16.method;
                        _tmp_16.this->baseInstance->baseClass->retain(_tmp_16.this->baseInstance);
                        BSG_MethodFatPtr__｢BSG_Int｣￫BSG_Int _tmp_17;
                        _tmp_17.this = (BSG_AnyInstance*)_tmp_22;
                        _tmp_17.method = &BSG_MethodCastShim__｢BSG_Any｣￫BSG_Any_castTo_｢BSG_Int｣￫BSG_Int;
                        if(_tmp_17.this) {
                            _tmp_17.this->baseInstance->baseClass->retain(_tmp_17.this->baseInstance);
                        }
                        if(this) {
                            this->baseInstance->baseClass->release(this->baseInstance);
                        }
                        if(_tmp_7.this) {
                            _tmp_7.this->baseInstance->baseClass->release(_tmp_7.this->baseInstance);
                        }
                        if(_tmp_12.this) {
                            _tmp_12.this->baseInstance->baseClass->release(_tmp_12.this->baseInstance);
                        }
                        if(_tmp_17.this) {
                            _tmp_17.this->baseInstance->baseClass->release(_tmp_17.this->baseInstance);
                        }
                        return;
                    }
                    #ifndef BSG_MethodCastShimDef_C__｢｣￫BSG_Any_castTo_｢｣￫BSG_Int
                    #define BSG_MethodCastShimDef_C__｢｣￫BSG_Any_castTo_｢｣￫BSG_Int
                    BSG_Int BSG_MethodCastShim__｢｣￫BSG_Any_castTo_｢｣￫BSG_Int(BSG_AnyInstance* this) {
                        BSG_Function__｢｣￫BSG_Any originalMethod = (BSG_Function__｢｣￫BSG_Any) ((BSG_Instance__BoxedMethod*)this)->method.method;
                        ((BSG_Instance__BoxedMethod*)this)->method.this->baseInstance->baseClass->retain(((BSG_Instance__BoxedMethod*)this)->method.this->baseInstance);
                        BSG_Any _tmp_8 = originalMethod(((BSG_Instance__BoxedMethod*)this)->method.this);
                        this->baseInstance->baseClass->release(this->baseInstance);
                        BSG_Int _tmp_9 = _tmp_8.content.primitive.IntValue;
                        return _tmp_9;
                    }
                    #endif
                    #ifndef BSG_MethodCastShimDef_C__｢BSG_Any｣￫BSG_Void_castTo_｢BSG_Int｣￫BSG_Void
                    #define BSG_MethodCastShimDef_C__｢BSG_Any｣￫BSG_Void_castTo_｢BSG_Int｣￫BSG_Void
                    BSG_Void BSG_MethodCastShim__｢BSG_Any｣￫BSG_Void_castTo_｢BSG_Int｣￫BSG_Void(BSG_AnyInstance* this,BSG_Int _tmp_13) {
                        BSG_Any _tmp_14;
                        _tmp_14.type = BSG_Any_ContentType__Primitive;
                        _tmp_14.content.primitive.IntValue = _tmp_13;
                        BSG_Function__｢BSG_Any｣￫BSG_Void originalMethod = (BSG_Function__｢BSG_Any｣￫BSG_Void) ((BSG_Instance__BoxedMethod*)this)->method.method;
                        ((BSG_Instance__BoxedMethod*)this)->method.this->baseInstance->baseClass->retain(((BSG_Instance__BoxedMethod*)this)->method.this->baseInstance);
                        originalMethod(((BSG_Instance__BoxedMethod*)this)->method.this,_tmp_14);
                        this->baseInstance->baseClass->release(this->baseInstance);
                    }
                    #endif
                    #ifndef BSG_MethodCastShimDef_C__｢BSG_Any｣￫BSG_Any_castTo_｢BSG_Int｣￫BSG_Int
                    #define BSG_MethodCastShimDef_C__｢BSG_Any｣￫BSG_Any_castTo_｢BSG_Int｣￫BSG_Int
                    BSG_Int BSG_MethodCastShim__｢BSG_Any｣￫BSG_Any_castTo_｢BSG_Int｣￫BSG_Int(BSG_AnyInstance* this,BSG_Int _tmp_18) {
                        BSG_Any _tmp_19;
                        _tmp_19.type = BSG_Any_ContentType__Primitive;
                        _tmp_19.content.primitive.IntValue = _tmp_18;
                        BSG_Function__｢BSG_Any｣￫BSG_Any originalMethod = (BSG_Function__｢BSG_Any｣￫BSG_Any) ((BSG_Instance__BoxedMethod*)this)->method.method;
                        ((BSG_Instance__BoxedMethod*)this)->method.this->baseInstance->baseClass->retain(((BSG_Instance__BoxedMethod*)this)->method.this->baseInstance);
                        BSG_Any _tmp_20 = originalMethod(((BSG_Instance__BoxedMethod*)this)->method.this,_tmp_19);
                        this->baseInstance->baseClass->release(this->baseInstance);
                        BSG_Int _tmp_21 = _tmp_20.content.primitive.IntValue;
                        return _tmp_21;
                    }
                    #endif
                    
                    // Class Singletons
                    struct BSG_Class__MethodCast BSG_ClassSingleton__MethodCast_MethodCast = {
                        .zeroArgNonVoid = &BSG_Method__MethodCast·zeroArgNonVoid,
                        .oneArgVoid = &BSG_Method__MethodCast·oneArgVoid,
                        .oneArgNonVoid = &BSG_Method__MethodCast·oneArgNonVoid,
                        .tester = &BSG_Method__MethodCast·tester,
                    };
                    
                    // Constructor
                    struct BSG_Instance__MethodCast* BSG_Constructor__MethodCast() {
                        struct BSG_BaseInstance__MethodCast* baseInstance = malloc(sizeof(struct BSG_BaseInstance__MethodCast));
                        baseInstance->refCount = 0;
                        baseInstance->MethodCast = (struct BSG_Instance__MethodCast) {
                            .baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
                            .class = &BSG_ClassSingleton__MethodCast_MethodCast,
                        };
                        baseInstance->baseClass = (struct BSG_AnyBaseClass*) &BSG_BaseClassSingleton__MethodCast;
                        return &baseInstance->MethodCast;
                    }
                    
                    // Singleton
                """.trimIndent() + "\n"
        )

        assertEquals(expectedOutput["MethodCast.h"], polyOutput["MethodCast.h"])
        assertEquals(expectedOutput["MethodCast.c"], polyOutput["MethodCast.c"])
    }
}