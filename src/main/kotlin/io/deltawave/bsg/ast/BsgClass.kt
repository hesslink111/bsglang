package io.deltawave.bsg.ast

import io.deltawave.bsg.ast.type.BsgType
import io.deltawave.bsg.context.AstContext
import io.deltawave.bsg.context.ClassMetadata
import io.deltawave.bsg.context.ClassScope
import io.deltawave.bsg.context.VarMetadata

data class BsgClass(
    val name: String,
    val directSuperClasses: List<String>,
    val body: BsgClassBody
) {
    fun toC(ctx: AstContext) {

        val classMeta = ctx.astMetadata.getClass(name)
        val classScope = ClassScope(classMeta)


        // Complete
        // Header basics
        ctx.hFile.appendLine("#ifndef BSG_H__$name")
        ctx.hFile.appendLine("#define BSG_H__$name")

        // Includes
        // TODO: include any supertypes.
        // TODO: Include the header that defines NULL -- in cFile.

        // Complete
        // Type number
        ctx.hFile.appendLine("const BSG_AnyType BSG_Type__$name = ${ctx.getNextTypeNum()}l;")

//        // Complete
        // Instance - Struct containing fields
        ctx.hFile.appendLine("struct BSG_Instance__$name {")
        ctx.hFile.appendLine("struct BSG_AnyBaseInstance* baseInstance;")
        ctx.hFile.appendLine("struct BSG_Class__${name}* class;")
        body.fields.forEach { field ->
            ctx.hFile.appendLine("${field.type.getCType()} ${field.name};")
        }
        ctx.hFile.appendLine("};")

        // Complete
        // BaseInstance - Struct containing sub-object instances, including self.
        ctx.hFile.appendLine("struct BSG_BaseInstance__$name {")
        ctx.hFile.appendLine("struct BSG_AnyBaseClass* baseClass;")
        ctx.hFile.appendLine("int refCount;")
        getSuperClassesAndSelf(ctx).forEach { cls ->
            ctx.hFile.appendLine("struct BSG_Instance__${cls.name} ${cls.name};")
        }
        ctx.hFile.appendLine("};")

        // Complete
        // Class - Struct containing pointers to non-overridden methods.
        ctx.hFile.appendLine("struct BSG_Class__$name {")
        ctx.astMetadata.getClass(name)
                .methods
                .values
                .filter { it.methodOf.name == name }
            .forEach { method ->
                val returnCType = (method.type as BsgType.Method).returnType.getCType()
                val thisArgType = "struct BSG_Instance__$name*"
                val otherArgTypes = method.type.argTypes.map { it.getCType() }
                val argTypes = (listOf(thisArgType) + otherArgTypes).joinToString(",")
                ctx.hFile.appendLine("$returnCType (*${method.varName})($argTypes);")
            }
        ctx.hFile.appendLine("};")

        // Complete
        // BaseMethods - Functions used in BaseClass. "cast", "retain", and "release" methods.
        // Cast
        ctx.hFile.appendLine("struct BSG_AnyInstance* BSG_BaseMethod__${name}_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);")
        ctx.cFile.appendLine("struct BSG_AnyInstance* BSG_BaseMethod__${name}_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {")
        ctx.cFile.appendLine("struct BSG_BaseInstance__$name* b = (struct BSG_BaseInstance__$name*)base;")
        ctx.cFile.appendLine("switch(type) {")
        getSuperClassesAndSelf(ctx).forEach { cls ->
            ctx.cFile.appendLine("case BSG_Type__${cls.name}:")
            ctx.cFile.appendLine("return (struct BSG_AnyInstance*)&b->${cls.name};")
        }
        ctx.cFile.appendLine("}")
        ctx.cFile.appendLine("return NULL;")
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
        getSuperClassesAndSelf(ctx).forEach { cls ->
            cls.fields.values.filter { it.fieldOf.name == cls.name }.forEach { field ->
                val fieldAccess = "b->${cls.name}.${field.varName}"
                // Release fields.
                if(field.type is BsgType.Class) {
                    ctx.cFile.appendLine("if($fieldAccess) {")
                    ctx.cFile.appendLine("$fieldAccess->baseInstance->baseClass->release($fieldAccess->baseInstance);")
                    ctx.cFile.appendLine("}")
                } else if(field.type is BsgType.Method) {
                    ctx.cFile.appendLine("if($fieldAccess.this) {")
                    ctx.cFile.appendLine("$fieldAccess.this->baseInstance->baseClass->release($fieldAccess.this->baseInstance);")
                    ctx.cFile.appendLine("}")
                }

            }
            ctx.cFile.appendLine("free(base);")
        }
        ctx.cFile.appendLine("}")
        ctx.cFile.appendLine("}")

        // Complete
        // BaseClassSingleton - Implementation of BaseClass struct, containing  of cast method.
        ctx.hFile.appendLine("extern struct BSG_BaseClass BSG_BaseClassSingleton__$name;")
        ctx.cFile.appendLine("struct BSG_BaseClass BSG_BaseClassSingleton__$name = {")
        ctx.cFile.appendLine(".cast = &BSG_BaseMethod__${name}_cast,")
        ctx.cFile.appendLine(".retain = &BSG_BaseMethod__${name}_retain,")
        ctx.cFile.appendLine(".release = &BSG_BaseMethod__${name}_release,")
        ctx.cFile.appendLine("};")

        // Complete
        // Methods - Functions for each method defined. Used in ClassSingleton.
        body.methods.filter { it.name in ctx.astMetadata.getClass(name).methods }.forEach { method ->
            val methodMeta = ctx.astMetadata.getClass(name).methods[method.name]!!
            methodMeta.type as BsgType.Method

            val thisName = if(methodMeta.methodOf.name != name) ctx.getUniqueVarName() else "this"
            val thisArg = "struct BSG_Instance__${methodMeta.methodOf.name}* $thisName"
            val otherArgs = method.arguments
                .map { (argName, type) -> "${type.getCType()} $argName" }
            val args = (listOf(thisArg) + otherArgs).joinToString(",")

            ctx.hFile.appendLine("${methodMeta.type.returnType.getCType()} ${getCMethodName(methodMeta)}($args);")
            ctx.cFile.appendLine("${methodMeta.type.returnType.getCType()} ${getCMethodName(methodMeta)}($args) {")
            if(methodMeta.methodOf.name != name) { // Add cast.
                ctx.cFile.appendLine("struct BSG_Instance__$name* this = (struct BSG_Instance__$name*)$thisName->baseClass->cast($thisName->baseClass, BSG_Type__$name);")
            }
            method.toC(ctx, classScope.methodScope(methodMeta))
            ctx.cFile.appendLine("}")

            // Fat pointer
            ctx.hFile.appendLine("struct BSG_MethodFatPtr__${methodMeta.methodOf.name}_${methodMeta.varName} {")
            ctx.hFile.appendLine("${methodMeta.methodOf.getCType()} this;")
            ctx.hFile.appendLine("${methodMeta.type.returnType.getCType()} (*method)($args);")
            ctx.hFile.appendLine("};")
        }

        // Complete
        // ClassSingletons - Implementation of Classes (all implemented) with method pointers set.
        getSuperClassesAndSelf(ctx).forEach { superCls ->
            ctx.hFile.appendLine("extern struct BSG_Class__${superCls.name} BSG_ClassSingleton__${name}_${superCls.name};")
            ctx.cFile.appendLine("struct BSG_Class__${superCls.name} BSG_ClassSingleton__${name}_${superCls.name} = {")
            superCls.methods.values.filter { it.methodOf.name == superCls.name }.forEach { m ->
                ctx.cFile.appendLine(".${m.varName} = &${getCMethodName(m)},")
            }
            ctx.cFile.appendLine("};")
        }

        // Complete
        // Constructor - Function for creating class.
        ctx.hFile.appendLine("extern struct BSG_Instance__$name* BSG_Constructor__$name();")
        ctx.cFile.appendLine("struct BSG_Instance__$name* BSG_Constructor__$name() {")
        ctx.cFile.appendLine("struct BSG_BaseInstance__$name* baseInstance = malloc(sizeof(struct BSG_BaseInstance__$name));")
        getSuperClassesAndSelf(ctx).forEach { cls ->
            ctx.cFile.appendLine("baseInstance->${cls.name} = (struct BSG_Instance__${cls.name}) {")
            ctx.cFile.appendLine(".baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,")
            ctx.cFile.appendLine(".class = &BSG_ClassSingleton__${name}_${cls.name},")
            ctx.cFile.appendLine("};")
        }
        ctx.cFile.appendLine("baseInstance->baseClass = (struct BSG_AnyBaseClass*) &BSG_BaseClassSingleton__$name;")
        // Initialize class and method fields to null.
        getSuperClassesAndSelf(ctx).forEach { cls ->
            cls.fields.values.filter { it.fieldOf.name == cls.name }.forEach { field ->
                val fieldAccess = "baseInstance->${cls.name}.${field.varName}"
                // Initialize fields to null.
                if(field.type is BsgType.Class) {
                    ctx.cFile.appendLine("$fieldAccess = NULL;")
                } else if(field.type is BsgType.Method) {
                    ctx.cFile.appendLine("$fieldAccess.this = NULL;")
                }
            }
        }
        ctx.cFile.appendLine("return &baseInstance->$name;")
        ctx.cFile.appendLine("}")

        // Complete
        // Footer
        ctx.hFile.appendLine("#endif")
    }

    private fun getSuperClassesAndSelf(ctx: AstContext): List<ClassMetadata> {
        return ctx.astMetadata.getClass(name).superTypes.map {
            it as BsgType.Class
            ctx.astMetadata.getClass(it.name)
        } + listOf(ctx.astMetadata.getClass(name))
    }

    private fun getCMethodName(methodMeta: VarMetadata.Method): String {
        return if(methodMeta.methodOf.name != name) {
            "BSG_Method__${name}_${methodMeta.methodOf.name}_${methodMeta.varName}"
        } else {
            "BSG_Method__${name}_${methodMeta.varName}"
        }
    }
}