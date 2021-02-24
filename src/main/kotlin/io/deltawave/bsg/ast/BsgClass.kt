package io.deltawave.bsg.ast

import io.deltawave.bsg.ast.type.BsgType
import io.deltawave.bsg.context.*
import io.deltawave.bsg.util.writelnNotBlank

data class BsgClass(
    val name: String,
    val directSuperClasses: List<BsgType.Class>,
    val body: BsgClassBody,
    val attributes: Set<String>
) {
    fun toC(ctx: ClassContext, globalScope: GlobalScope, hSources: List<BsgHeaderStatement>) {
        val classMeta = ctx.astMetadata.getClass(name)
        val classScope = ClassScope(globalScope, classMeta)

        // Header basics
        ctx.hIfNDef.writeln("#ifndef BSG_H__$name")
        ctx.hIfNDef.writeln("#define BSG_H__$name")

        // Includes
        ctx.hIncludes.writeln("#include \"bsg_preamble.h\"")
        ctx.cIncludes.writeln("#include \"${name}.h\"")
        hSources.forEach { it.toC(ctx) }

        // Type number
        ctx.hTypeNum.writeln("#define BSG_Type__$name ${ctx.getNextTypeNum()}l")

        // Instance - Struct containing fields
        ctx.astMetadata.getClass(name).genericType.getCDefinitions(ctx.astMetadata).forEach {
            ctx.hInstance.writelnNotBlank(it)
        }

        // BaseInstance - Struct containing sub-object instances, including self.
        ctx.hBaseInstance.writeln_r("struct BSG_BaseInstance__$name {")
        ctx.hBaseInstance.writeln("struct BSG_AnyBaseClass* baseClass;")
        ctx.hBaseInstance.writeln("int refCount;")
        getSelfAndSuperClasses(ctx).forEach { cls ->
            ctx.hBaseInstance.writeln("struct BSG_Instance__${cls.name} ${cls.name};")
        }
        ctx.hBaseInstance.writeln_l("};")

        // Method definitions
        body.methods.filter { it.name in ctx.astMetadata.getClass(name).methods }
                .flatMap { it.getType(ctx, classMeta.genericType).getCDefinitions(ctx.astMetadata) }
                .distinct()
                .forEach { methodDef ->
                    ctx.hMethodTypedefs.writelnNotBlank(methodDef)
                }

        // Class - Struct containing pointers to non-overridden methods.
        ctx.hClass.writeln_r("struct BSG_Class__$name {")
        ctx.astMetadata.getClass(name)
                .methods
                .values
                .filter { it.methodOf.name == name }
            .forEach { method ->
                method.type as BsgType.Method
                ctx.hClass.writeln("${method.type.getFName()} ${method.varName};")
            }
        ctx.hClass.writeln_l("};")

        // BaseMethods - Functions used in BaseClass. "cast", "retain", and "release" methods.
        // Cast
        ctx.hBaseMethods.writeln("struct BSG_AnyInstance* BSG_BaseMethod__${name}_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);")

        ctx.cBaseMethods.writeln_r("struct BSG_AnyInstance* BSG_BaseMethod__${name}_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {")
        ctx.cBaseMethods.writeln("struct BSG_BaseInstance__$name* b = (struct BSG_BaseInstance__$name*)base;")
        ctx.cBaseMethods.writeln_r("switch(type) {")
        getSelfAndSuperClasses(ctx).forEach { cls ->
            ctx.cBaseMethods.writeln_r("case BSG_Type__${cls.name}:")
            ctx.cBaseMethods.writeln("return (struct BSG_AnyInstance*)&b->${cls.name};")
            ctx.cBaseMethods.indentLeft()
        }
        ctx.cBaseMethods.writeln_l("}")
        ctx.cBaseMethods.writeln("return NULL;")
        ctx.cBaseMethods.writeln_l("}")

        // CanCast
        ctx.hBaseMethods.writeln("BSG_Bool BSG_BaseMethod__${name}_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);")
        ctx.cBaseMethods.writeln_r("BSG_Bool BSG_BaseMethod__${name}_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {")
        ctx.cBaseMethods.writeln("struct BSG_BaseInstance__$name* b = (struct BSG_BaseInstance__$name*)base;")
        ctx.cBaseMethods.writeln_r("switch(type) {")
        getSelfAndSuperClasses(ctx).forEach { cls ->
            ctx.cBaseMethods.writeln_r("case BSG_Type__${cls.name}:")
            ctx.cBaseMethods.writeln("return true;")
            ctx.cBaseMethods.indentLeft()
        }
        ctx.cBaseMethods.writeln_l("}")
        ctx.cBaseMethods.writeln("return false;")
        ctx.cBaseMethods.writeln_l("}")

        // Retain
        ctx.hBaseMethods.writeln("void BSG_BaseMethod__${name}_retain(struct BSG_AnyBaseInstance* base);")
        ctx.cBaseMethods.writeln_r("void BSG_BaseMethod__${name}_retain(struct BSG_AnyBaseInstance* base) {")
        ctx.cBaseMethods.writeln("base->refCount++;")
        ctx.cBaseMethods.writeln_l("}")

        // Release
        ctx.hBaseMethods.writeln("void BSG_BaseMethod__${name}_release(struct BSG_AnyBaseInstance* base);")
        ctx.cBaseMethods.writeln_r("void BSG_BaseMethod__${name}_release(struct BSG_AnyBaseInstance* base) {")
        ctx.cBaseMethods.writeln("base->refCount--;")
        ctx.cBaseMethods.writeln_r("if(base->refCount <= 0) {")
        ctx.cBaseMethods.writeln("struct BSG_BaseInstance__$name* b = (struct BSG_BaseInstance__$name*)base;")
        body.methods.firstOrNull { it.name == "deinit" }?.let { deinitMethod ->
            if(deinitMethod.arguments.isNotEmpty()) {
                error("deinit method must take no arguments.")
            }
            ctx.cBaseMethods.writeln("base->refCount += 2;") // Hack to make sure deinit doesn't try to re-release. // TODO: No hacks.
            ctx.cBaseMethods.writeln("struct BSG_Instance__$name* this = &b->$name;")
            ctx.cBaseMethods.writeln("this->class->deinit((BSG_AnyInstancePtr)this);")
            ctx.cBaseMethods.writeln("base->refCount--;") // End hack.
        }
        getSelfAndSuperClasses(ctx)
                .flatMap { cls -> cls.fields.values.map { cls to it } }
                .forEach { (cls, field) ->
                    val fieldVar = "b->${cls.name}.${field.varName}"
                    // Release fields.
                    field.type.writeCRelease(fieldVar, ctx.cBaseMethods)
                }
        ctx.cBaseMethods.writeln("free(base);")
        ctx.cBaseMethods.writeln_l("}")
        ctx.cBaseMethods.writeln_l("}")

        // BaseClassSingleton - Implementation of BaseClass struct, containing  of cast method.
        ctx.hBaseClassSingleton.writeln("extern struct BSG_BaseClass BSG_BaseClassSingleton__$name;")
        ctx.cBaseClassSingleton.writeln_r("struct BSG_BaseClass BSG_BaseClassSingleton__$name = {")
        ctx.cBaseClassSingleton.writeln(".cast = &BSG_BaseMethod__${name}_cast,")
        ctx.cBaseClassSingleton.writeln(".canCast = &BSG_BaseMethod__${name}_canCast,")
        ctx.cBaseClassSingleton.writeln(".retain = &BSG_BaseMethod__${name}_retain,")
        ctx.cBaseClassSingleton.writeln(".release = &BSG_BaseMethod__${name}_release,")
        ctx.cBaseClassSingleton.writeln_l("};")

        // Methods - Functions for each method defined. Used in ClassSingleton.
        body.methods.filter { it.name in ctx.astMetadata.getClass(name).methods }.forEach { method ->
            val methodMeta = ctx.astMetadata.getClass(name).methods[method.name]!!
            method.toC(ctx, classScope.methodScope(methodMeta))
        }

        // ClassSingletons - Implementation of Classes (all implemented) with method pointers set.
        getSelfAndSuperClasses(ctx).forEach { superCls ->
            ctx.hClassSingletons.writeln("extern struct BSG_Class__${superCls.name} BSG_ClassSingleton__${name}_${superCls.name};")
            ctx.cClassSingletons.writeln_r("struct BSG_Class__${superCls.name} BSG_ClassSingleton__${name}_${superCls.name} = {")
            superCls.methods.values.filter { it.methodOf.name == superCls.name }.forEach { m ->
                // Find nearest overriding class.
                val oClass = getSelfAndSuperClasses(ctx)
                        .mapNotNull { c -> c.methods[m.varName]?.let { c } }
                        .first()
                ctx.cClassSingletons.writeln(".${m.varName} = &BSG_Method__${oClass.name}·${m.varName},")
            }
            ctx.cClassSingletons.writeln_l("};")
        }

        // Constructor - Function for creating class.
        ctx.hConstructor.writeln("extern struct BSG_Instance__$name* BSG_Constructor__$name();")
        ctx.cConstructor.writeln_r("struct BSG_Instance__$name* BSG_Constructor__$name() {")
        ctx.cConstructor.writeln("struct BSG_BaseInstance__$name* baseInstance = malloc(sizeof(struct BSG_BaseInstance__$name));")
        getSelfAndSuperClasses(ctx).forEach { cls ->
            ctx.cConstructor.writeln_r("baseInstance->${cls.name} = (struct BSG_Instance__${cls.name}) {")
            ctx.cConstructor.writeln(".baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,")
            ctx.cConstructor.writeln(".class = &BSG_ClassSingleton__${name}_${cls.name},")
            ctx.cConstructor.writeln_l("};")
        }
        ctx.cConstructor.writeln("baseInstance->baseClass = (struct BSG_AnyBaseClass*) &BSG_BaseClassSingleton__$name;")
        // Initialize class and method fields to null.
        getSelfAndSuperClasses(ctx).forEach { cls ->
            cls.fields.values.filter { it.fieldOf.name == cls.name }.forEach { field ->
                val fieldAccess = "baseInstance->${cls.name}.${field.varName}"
                // Initialize fields to null.
                when (field.type) {
                    is BsgType.Class -> ctx.cConstructor.writeln("$fieldAccess = NULL;")
                    is BsgType.Method -> ctx.cConstructor.writeln("$fieldAccess.this = NULL;")
                    is BsgType.Any -> {
                        ctx.cConstructor.writeln("$fieldAccess.type = BSG_Any_ContentType__Primitive;")
                        ctx.cConstructor.writeln("$fieldAccess.content.primitive.IntValue = 0;")
                    }
                    is BsgType.Primitive -> {} // No need to initialize.
                }
            }
        }
        // Call init method.
        body.methods.firstOrNull { it.name == "init" }?.let { deinitMethod ->
            if(deinitMethod.arguments.isNotEmpty()) {
                error("init method must take no arguments.")
            }
            ctx.cConstructor.writeln("baseInstance->refCount += 2;") // Hack to make sure init doesn't try to release. // TODO: No hacks.
            ctx.cConstructor.writeln("struct BSG_Instance__$name* this = &baseInstance->$name;")
            ctx.cConstructor.writeln("this->class->init((BSG_AnyInstancePtr)this);")
            ctx.cConstructor.writeln("baseInstance->refCount--;") // End hack.
        }
        ctx.cConstructor.writeln("return &baseInstance->$name;")
        ctx.cConstructor.writeln_l("}")

        // Global variables
        if("Singleton" in attributes) {
            ctx.hSingleton.writeln("extern struct BSG_Instance__$name* $name;")
            ctx.cSingleton.writeln("struct BSG_Instance__$name* $name = NULL;")
            ctx.mainHIncludes.writeln("#include \"$name.h\"")
            ctx.mainCInit.writeln("$name = BSG_Constructor__$name();")
            ctx.mainCInit.writeln("$name->baseInstance->baseClass->retain($name->baseInstance);")
            ctx.mainCDeinit.writeln("$name->baseInstance->baseClass->release($name->baseInstance);")
        }

        // Main
        body.takeIf { "Singleton" in attributes }
                ?.methods?.firstOrNull { "Main" in it.attributes }
                ?.let { mainMethod ->
                    if(mainMethod.arguments.isNotEmpty()) {
                        error("Arguments must be empty in main method.")
                    }
                    ctx.mainCBody.writeln("$name->baseInstance->baseClass->retain($name->baseInstance);")
                    ctx.mainCBody.writeln("$name->class->${mainMethod.name}((BSG_AnyInstancePtr)$name);")
                }

        // Footer
        ctx.hEndIf.writeln("#endif")
    }

    private fun getSelfAndSuperClasses(ctx: ClassContext): List<ClassMetadata> {
        return listOf(ctx.astMetadata.getClass(name)) + ctx.astMetadata.getClass(name).superTypes.map {
            it as BsgType.Class
            ctx.astMetadata.getClass(it.name)
        }
    }
}