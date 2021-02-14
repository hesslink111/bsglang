package io.deltawave.bsg.ast

import io.deltawave.bsg.ast.type.BsgType
import io.deltawave.bsg.context.*
import io.deltawave.bsg.util.appendLineNotBlank

data class BsgClass(
    val name: String,
    val directSuperClasses: List<String>,
    val body: BsgClassBody,
    val attributes: Set<String>
) {
    fun toC(ctx: ClassContext, globalScope: GlobalScope, hSources: List<BsgHeaderStatement>) {
        val classMeta = ctx.astMetadata.getClass(name)
        val classScope = ClassScope(globalScope, classMeta)

        // Header basics
        ctx.hFile.appendLine("#ifndef BSG_H__$name")
        ctx.hFile.appendLine("#define BSG_H__$name")

        // Includes
        ctx.hFile.appendLine("#include \"bsg_preamble.h\"")
        ctx.cFile.appendLine("#include \"${name}.h\"")
        hSources.forEach { it.toC(ctx) }

        // Type number
        ctx.hFile.appendLine("#define BSG_Type__$name ${ctx.getNextTypeNum()}l")

        // Instance - Struct containing fields
        ctx.astMetadata.getClass(name).type.getCDefinitions(ctx.astMetadata).forEach {
            ctx.hFile.appendLineNotBlank(it)
        }

        // BaseInstance - Struct containing sub-object instances, including self.
        ctx.hFile.appendLine("struct BSG_BaseInstance__$name {")
        ctx.hFile.appendLine("struct BSG_AnyBaseClass* baseClass;")
        ctx.hFile.appendLine("int refCount;")
        getSelfAndSuperClasses(ctx).forEach { cls ->
            ctx.hFile.appendLine("struct BSG_Instance__${cls.name} ${cls.name};")
        }
        ctx.hFile.appendLine("};")

        // Method definitions
        body.methods.filter { it.name in ctx.astMetadata.getClass(name).methods }
                .flatMap { it.getType(ctx, classMeta.type).getCDefinitions(ctx.astMetadata) }
                .distinct()
                .forEach { methodDef ->
                    ctx.hFile.appendLineNotBlank(methodDef)
                }

        // Class - Struct containing pointers to non-overridden methods.
        ctx.hFile.appendLine("struct BSG_Class__$name {")
        ctx.astMetadata.getClass(name)
                .methods
                .values
                .filter { it.methodOf.name == name }
            .forEach { method ->
                method.type as BsgType.Method
                ctx.hFile.appendLine("${method.type.getFName()} ${method.varName};")
            }
        ctx.hFile.appendLine("};")

        // BaseMethods - Functions used in BaseClass. "cast", "retain", and "release" methods.
        // Cast
        ctx.hFile.appendLine("struct BSG_AnyInstance* BSG_BaseMethod__${name}_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);")
        ctx.cFile.appendLine("struct BSG_AnyInstance* BSG_BaseMethod__${name}_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {")
        ctx.cFile.appendLine("struct BSG_BaseInstance__$name* b = (struct BSG_BaseInstance__$name*)base;")
        ctx.cFile.appendLine("switch(type) {")
        getSelfAndSuperClasses(ctx).forEach { cls ->
            ctx.cFile.appendLine("case BSG_Type__${cls.name}:")
            ctx.cFile.appendLine("return (struct BSG_AnyInstance*)&b->${cls.name};")
        }
        ctx.cFile.appendLine("}")
        ctx.cFile.appendLine("return NULL;")
        ctx.cFile.appendLine("}")
        // CanCast
        ctx.hFile.appendLine("BSG_Bool BSG_BaseMethod__${name}_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);")
        ctx.cFile.appendLine("BSG_Bool BSG_BaseMethod__${name}_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {")
        ctx.cFile.appendLine("struct BSG_BaseInstance__$name* b = (struct BSG_BaseInstance__$name*)base;")
        ctx.cFile.appendLine("switch(type) {")
        getSelfAndSuperClasses(ctx).forEach { cls ->
            ctx.cFile.appendLine("case BSG_Type__${cls.name}:")
            ctx.cFile.appendLine("return true;")
        }
        ctx.cFile.appendLine("}")
        ctx.cFile.appendLine("return false;")
        ctx.cFile.appendLine("}")
        // Retain
        ctx.hFile.appendLine("void BSG_BaseMethod__${name}_retain(struct BSG_AnyBaseInstance* base);")
        ctx.cFile.appendLine("void BSG_BaseMethod__${name}_retain(struct BSG_AnyBaseInstance* base) {")
        ctx.cFile.appendLine("base->refCount++;")
        ctx.cFile.appendLine("}")
        // Release
        ctx.hFile.appendLine("void BSG_BaseMethod__${name}_release(struct BSG_AnyBaseInstance* base);")
        ctx.cFile.appendLine("void BSG_BaseMethod__${name}_release(struct BSG_AnyBaseInstance* base) {")
        ctx.cFile.appendLine("base->refCount--;")
        ctx.cFile.appendLine("if(base->refCount <= 0) {")
        ctx.cFile.appendLine("struct BSG_BaseInstance__$name* b = (struct BSG_BaseInstance__$name*)base;")
        body.methods.firstOrNull { it.name == "deinit" }?.let { deinitMethod ->
            assert(deinitMethod.arguments.isEmpty()) { "deinit method must take no arguments." }
            ctx.cFile.appendLine("base->refCount += 2;") // Hack to make sure deinit doesn't try to re-release. // TODO: No hacks.
            ctx.cFile.appendLine("struct BSG_Instance__$name* this = &b->$name;")
            ctx.cFile.appendLine("this->class->deinit((BSG_AnyInstancePtr)this);")
            ctx.cFile.appendLine("base->refCount--;") // End hack.
        }
        getSelfAndSuperClasses(ctx)
                .flatMap { cls -> cls.fields.values.map { cls to it } }
                .forEach { (cls, field) ->
                    val fieldVar = "b->${cls.name}.${field.varName}"
                    // Release fields.
                    ctx.cFile.appendLineNotBlank(field.type.getCRelease(fieldVar))
                }
        ctx.cFile.appendLine("free(base);")
        ctx.cFile.appendLine("}")
        ctx.cFile.appendLine("}")

        // BaseClassSingleton - Implementation of BaseClass struct, containing  of cast method.
        ctx.hFile.appendLine("extern struct BSG_BaseClass BSG_BaseClassSingleton__$name;")
        ctx.cFile.appendLine("struct BSG_BaseClass BSG_BaseClassSingleton__$name = {")
        ctx.cFile.appendLine(".cast = &BSG_BaseMethod__${name}_cast,")
        ctx.cFile.appendLine(".canCast = &BSG_BaseMethod__${name}_canCast,")
        ctx.cFile.appendLine(".retain = &BSG_BaseMethod__${name}_retain,")
        ctx.cFile.appendLine(".release = &BSG_BaseMethod__${name}_release,")
        ctx.cFile.appendLine("};")

        // Methods - Functions for each method defined. Used in ClassSingleton.
        body.methods.filter { it.name in ctx.astMetadata.getClass(name).methods }.forEach { method ->
            val methodMeta = ctx.astMetadata.getClass(name).methods[method.name]!!
            method.toC(ctx, classScope.methodScope(methodMeta))
        }

        // ClassSingletons - Implementation of Classes (all implemented) with method pointers set.
        getSelfAndSuperClasses(ctx).forEach { superCls ->
            ctx.hFile.appendLine("extern struct BSG_Class__${superCls.name} BSG_ClassSingleton__${name}_${superCls.name};")
            ctx.cFile.appendLine("struct BSG_Class__${superCls.name} BSG_ClassSingleton__${name}_${superCls.name} = {")
            superCls.methods.values.filter { it.methodOf.name == superCls.name }.forEach { m ->
                // Find nearest overriding class.
                val oClass = getSelfAndSuperClasses(ctx)
                        .mapNotNull { c -> c.methods[m.varName]?.let { c } }
                        .first()
                ctx.cFile.appendLine(".${m.varName} = &BSG_Method__${oClass.name}·${m.varName},")
            }
            ctx.cFile.appendLine("};")
        }

        // Constructor - Function for creating class.
        ctx.hFile.appendLine("extern struct BSG_Instance__$name* BSG_Constructor__$name();")
        ctx.cFile.appendLine("struct BSG_Instance__$name* BSG_Constructor__$name() {")
        ctx.cFile.appendLine("struct BSG_BaseInstance__$name* baseInstance = malloc(sizeof(struct BSG_BaseInstance__$name));")
        getSelfAndSuperClasses(ctx).forEach { cls ->
            ctx.cFile.appendLine("baseInstance->${cls.name} = (struct BSG_Instance__${cls.name}) {")
            ctx.cFile.appendLine(".baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,")
            ctx.cFile.appendLine(".class = &BSG_ClassSingleton__${name}_${cls.name},")
            ctx.cFile.appendLine("};")
        }
        ctx.cFile.appendLine("baseInstance->baseClass = (struct BSG_AnyBaseClass*) &BSG_BaseClassSingleton__$name;")
        // Initialize class and method fields to null.
        getSelfAndSuperClasses(ctx).forEach { cls ->
            cls.fields.values.filter { it.fieldOf.name == cls.name }.forEach { field ->
                val fieldAccess = "baseInstance->${cls.name}.${field.varName}"
                // Initialize fields to null.
                when (field.type) {
                    is BsgType.Class -> ctx.cFile.appendLine("$fieldAccess = NULL;")
                    is BsgType.Method -> ctx.cFile.appendLine("$fieldAccess.this = NULL;")
                    is BsgType.Any -> {
                        ctx.cFile.appendLine("$fieldAccess.type = BSG_Any_ContentType__Primitive;")
                        ctx.cFile.appendLine("$fieldAccess.content.primitive.IntValue = 0;")
                    }
                    is BsgType.Primitive -> {} // No need to initialize.
                }
            }
        }
        // Call init method.
        body.methods.firstOrNull { it.name == "init" }?.let { deinitMethod ->
            assert(deinitMethod.arguments.isEmpty()) { "init method must take no arguments." }
            ctx.cFile.appendLine("baseInstance->refCount += 2;") // Hack to make sure init doesn't try to release. // TODO: No hacks.
            ctx.cFile.appendLine("struct BSG_Instance__$name* this = &baseInstance->$name;")
            ctx.cFile.appendLine("this->class->init((BSG_AnyInstancePtr)this);")
            ctx.cFile.appendLine("baseInstance->refCount--;") // End hack.
        }
        ctx.cFile.appendLine("return &baseInstance->$name;")
        ctx.cFile.appendLine("}")

        // Global variables
        if("Singleton" in attributes) {
            ctx.hFile.appendLine("extern struct BSG_Instance__$name* $name;")
            ctx.cFile.appendLine("struct BSG_Instance__$name* $name = NULL;")
            ctx.mainHFile.appendLine("#include \"$name.h\"")
            ctx.mainCFileInit.appendLine("$name = BSG_Constructor__$name();")
            ctx.mainCFileInit.appendLine("$name->baseInstance->baseClass->retain($name->baseInstance);")
            ctx.mainCFileDeinit.appendLine("$name->baseInstance->baseClass->release($name->baseInstance);")
        }

        // Main
        body.takeIf { "Singleton" in attributes }
                ?.methods?.firstOrNull { "Main" in it.attributes }
                ?.let { mainMethod ->
                    assert(mainMethod.arguments.isEmpty()) { "Arguments must be empty in main method." }
                    ctx.mainCFileMain.appendLine("$name->baseInstance->baseClass->retain($name->baseInstance);")
                    ctx.mainCFileMain.appendLine("$name->class->${mainMethod.name}((BSG_AnyInstancePtr)$name);")
                }

        // Footer
        ctx.hFile.appendLine("#endif")
    }

    private fun getSelfAndSuperClasses(ctx: ClassContext): List<ClassMetadata> {
        return listOf(ctx.astMetadata.getClass(name)) + ctx.astMetadata.getClass(name).superTypes.map {
            it as BsgType.Class
            ctx.astMetadata.getClass(it.name)
        }
    }
}