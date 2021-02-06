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
            #include "bsg_preamble.h"
            const BSG_AnyType BSG_Type__Empty;
            struct BSG_Instance__Empty {
            struct BSG_AnyBaseInstance* baseInstance;
            struct BSG_Class__Empty* class;
            };
            struct BSG_BaseInstance__Empty {
            struct BSG_AnyBaseClass* baseClass;
            int refCount;
            struct BSG_Instance__Empty Empty;
            };
            struct BSG_Class__Empty {
            };
            struct BSG_AnyInstance* BSG_BaseMethod__Empty_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
            void BSG_BaseMethod__Empty_retain(struct BSG_AnyBaseInstance* base);
            void BSG_BaseMethod__Empty_release(struct BSG_AnyBaseInstance* base);
            extern struct BSG_BaseClass BSG_BaseClassSingleton__Empty;
            extern struct BSG_Class__Empty BSG_ClassSingleton__Empty_Empty;
            extern struct BSG_Instance__Empty* BSG_Constructor__Empty();
            #endif
        """.trimIndent() + "\n"

        val expectedEmptyC = """
            #include "Empty.h"
            const BSG_AnyType BSG_Type__Empty = 0l;
            struct BSG_AnyInstance* BSG_BaseMethod__Empty_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
            struct BSG_BaseInstance__Empty* b = (struct BSG_BaseInstance__Empty*)base;
            switch(type) {
            case BSG_Type__Empty:
            return (struct BSG_AnyInstance*)&b->Empty;
            }
            return NULL;
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
            struct BSG_BaseClass BSG_BaseClassSingleton__Empty = {
            .cast = &BSG_BaseMethod__Empty_cast,
            .retain = &BSG_BaseMethod__Empty_retain,
            .release = &BSG_BaseMethod__Empty_release,
            };
            struct BSG_Class__Empty BSG_ClassSingleton__Empty_Empty = {
            };
            struct BSG_Instance__Empty* BSG_Constructor__Empty() {
            struct BSG_BaseInstance__Empty* baseInstance = malloc(sizeof(struct BSG_BaseInstance__Empty));
            baseInstance->Empty = (struct BSG_Instance__Empty) {
            .baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
            .class = &BSG_ClassSingleton__Empty_Empty,
            };
            baseInstance->baseClass = (struct BSG_AnyBaseClass*) &BSG_BaseClassSingleton__Empty;
            return &baseInstance->Empty;
            }
        """.trimIndent() + "\n"

        assertEquals(expectedEmptyH, Compiler.compile(inputFiles)["Empty.h"])
        assertEquals(expectedEmptyC, Compiler.compile(inputFiles)["Empty.c"])
    }

    @Test
    fun singletonClass_generates() {
        val inputFiles = mapOf(
                "Empty.bsg" to """
                    [Singleton]
                    class Empty {}
                """.trimIndent()
        )

        val expectedMainH = """
            #include "Empty.h"
        """.trimIndent() + "\n"

        val expectedMainC = """
            #include "main.h"
            int main() {
            Empty = BSG_Constructor__Empty();
            Empty->baseInstance->baseClass->retain(Empty->baseInstance);
            Empty->baseInstance->baseClass->release(Empty->baseInstance);
            }
        """.trimIndent() + "\n"

        assertEquals(expectedMainH, Compiler.compile(inputFiles)["main.h"])
        assertEquals(expectedMainC, Compiler.compile(inputFiles)["main.c"])
    }
}