package io.deltawave.bsg.ast.type

import io.deltawave.bsg.context.AstMetadata
import io.deltawave.bsg.context.ClassContext
import io.deltawave.bsg.util.writelnNotBlank
import org.ainslec.picocog.PicoWriter

sealed class BsgType {
    object Any: BsgType() {
        override fun getCDefinitions(astMetadata: AstMetadata): List<String> {
            return emptyList()
        }

        override fun getCTypeInternal(): String {
            return "BSG_Any"
        }

        override fun writeCRetain(varName: String, writer: PicoWriter) {
            writer.writeln_r("if($varName.type == BSG_Any_ContentType__Instance && $varName.content.instance) {")
            writer.writeln("$varName.content.instance->baseInstance->baseClass->retain($varName.content.instance->baseInstance);")
            writer.writeln_lr("} else if($varName.type == BSG_Any_ContentType__Method && $varName.content.method.this) {")
            writer.writeln("$varName.content.method.this->baseInstance->baseClass->retain($varName.content.method.this->baseInstance);")
            writer.writeln_l("}")
        }

        override fun writeCRelease(varName: String, writer: PicoWriter) {
            writer.writeln_r("if($varName.type == BSG_Any_ContentType__Instance && $varName.content.instance) {")
            writer.writeln("$varName.content.instance->baseInstance->baseClass->release($varName.content.instance->baseInstance);")
            writer.writeln_lr("} else if($varName.type == BSG_Any_ContentType__Method && $varName.content.method.this) {")
            writer.writeln("$varName.content.method.this->baseInstance->baseClass->release($varName.content.method.this->baseInstance);")
            writer.writeln_l("}")
        }

        override fun writeCCast(fromVar: String, toType: BsgType, toVar: String, ctx: ClassContext, cCurrentMethodWriter: PicoWriter, hNewMethodWriter: PicoWriter, cNewMethodWriter: PicoWriter) {
            when(toType) {
                Any -> cCurrentMethodWriter.writeln("${toType.getCType()} $toVar = $fromVar;")
                is Class -> cCurrentMethodWriter.writeln("${toType.getCType()} $toVar = (${toType.getCType()}) $fromVar.content.instance->baseInstance->baseClass->cast($fromVar.content.instance->baseInstance, BSG_Type__${toType.name});")
                is Primitive -> {
                    // TODO: Function in preamble.c.
                    cCurrentMethodWriter.writeln("${toType.getCType()} $toVar;")
                    cCurrentMethodWriter.writeln_r("switch($fromVar.type) {")
                    cCurrentMethodWriter.writeln_r("case BSG_Any_ContentType__Char:")
                    cCurrentMethodWriter.writeln("$toVar = (${toType.getCType()}) $fromVar.content.primitive.CharValue;")
                    cCurrentMethodWriter.writeln("break;")
                    cCurrentMethodWriter.indentLeft()
                    cCurrentMethodWriter.writeln_r("case BSG_Any_ContentType__Byte:")
                    cCurrentMethodWriter.writeln("$toVar = (${toType.getCType()}) $fromVar.content.primitive.ByteValue;")
                    cCurrentMethodWriter.writeln("break;")
                    cCurrentMethodWriter.indentLeft()
                    cCurrentMethodWriter.writeln_r("case BSG_Any_ContentType__Short:")
                    cCurrentMethodWriter.writeln("$toVar = (${toType.getCType()}) $fromVar.content.primitive.ShortValue;")
                    cCurrentMethodWriter.writeln("break;")
                    cCurrentMethodWriter.indentLeft()
                    cCurrentMethodWriter.writeln_r("case BSG_Any_ContentType__Int:")
                    cCurrentMethodWriter.writeln("$toVar = (${toType.getCType()}) $fromVar.content.primitive.IntValue;")
                    cCurrentMethodWriter.writeln("break;")
                    cCurrentMethodWriter.indentLeft()
                    cCurrentMethodWriter.writeln_r("case BSG_Any_ContentType__Long:")
                    cCurrentMethodWriter.writeln("$toVar = (${toType.getCType()}) $fromVar.content.primitive.LongValue;")
                    cCurrentMethodWriter.writeln("break;")
                    cCurrentMethodWriter.indentLeft()
                    cCurrentMethodWriter.writeln_r("case BSG_Any_ContentType__UByte:")
                    cCurrentMethodWriter.writeln("$toVar = (${toType.getCType()}) $fromVar.content.primitive.UByteValue;")
                    cCurrentMethodWriter.writeln("break;")
                    cCurrentMethodWriter.indentLeft()
                    cCurrentMethodWriter.writeln_r("case BSG_Any_ContentType__UShort:")
                    cCurrentMethodWriter.writeln("$toVar = (${toType.getCType()}) $fromVar.content.primitive.UShortValue;")
                    cCurrentMethodWriter.writeln("break;")
                    cCurrentMethodWriter.indentLeft()
                    cCurrentMethodWriter.writeln_r("case BSG_Any_ContentType__UInt:")
                    cCurrentMethodWriter.writeln("$toVar = (${toType.getCType()}) $fromVar.content.primitive.UIntValue;")
                    cCurrentMethodWriter.writeln("break;")
                    cCurrentMethodWriter.indentLeft()
                    cCurrentMethodWriter.writeln_r("case BSG_Any_ContentType__ULong:")
                    cCurrentMethodWriter.writeln("$toVar = (${toType.getCType()}) $fromVar.content.primitive.ULongValue;")
                    cCurrentMethodWriter.writeln("break;")
                    cCurrentMethodWriter.indentLeft()
                    cCurrentMethodWriter.writeln_r("case BSG_Any_ContentType__Float:")
                    cCurrentMethodWriter.writeln("$toVar = (${toType.getCType()}) $fromVar.content.primitive.FloatValue;")
                    cCurrentMethodWriter.writeln("break;")
                    cCurrentMethodWriter.indentLeft()
                    cCurrentMethodWriter.writeln_r("case BSG_Any_ContentType__Double:")
                    cCurrentMethodWriter.writeln("$toVar = (${toType.getCType()}) $fromVar.content.primitive.DoubleValue;")
                    cCurrentMethodWriter.writeln("break;")
                    cCurrentMethodWriter.indentLeft()
                    cCurrentMethodWriter.writeln_r("case BSG_Any_ContentType__Bool:")
                    cCurrentMethodWriter.writeln("$toVar = (${toType.getCType()}) $fromVar.content.primitive.BoolValue;")
                    cCurrentMethodWriter.writeln("break;")
                    cCurrentMethodWriter.indentLeft()
                    cCurrentMethodWriter.writeln_r("case BSG_Any_ContentType__Opaque:")
                    cCurrentMethodWriter.writeln("$toVar = (${toType.getCType()}) $fromVar.content.primitive.OpaqueValue;")
                    cCurrentMethodWriter.writeln("break;")
                    cCurrentMethodWriter.indentLeft()
                    cCurrentMethodWriter.writeln("case BSG_Any_ContentType__Instance:")
                    cCurrentMethodWriter.writeln("case BSG_Any_ContentType__Method:")
                    cCurrentMethodWriter.writeln_r("default:")
                    // Throw error
                    cCurrentMethodWriter.writeln("printf(\"Attempted to cast from Instance or Method to Primitive.\\n\");")
                    cCurrentMethodWriter.writeln("exit(1);")
                    cCurrentMethodWriter.indentLeft()
                    cCurrentMethodWriter.writeln_l("}")
                }
                is Method -> cCurrentMethodWriter.writeln("${toType.getCType()} $toVar = (${toType.getCType()}) $fromVar.content.method;")
                is Generic -> writeCCast(fromVar, toType.rawType, toVar, ctx, cCurrentMethodWriter, hNewMethodWriter, cNewMethodWriter)
            }
        }

        override fun getCInstanceOf(fromVar: String, isType: BsgType, toVar: String): String {
            return when(isType) {
                Any -> "BSG_Bool $toVar = true;"
                is Class -> "BSG_Bool $toVar = $fromVar.type == BSG_Any_ContentType__Instance && $fromVar.content.instance->baseInstance->baseClass->canCast($fromVar.content.instance->baseInstance, BSG_Type__${isType.name});"
                is Primitive -> "BSG_Bool $toVar = true;" // Primitives can all be casted to each other.
                is Method -> error("'Any is MethodType' is not yet supported.")
                is Generic -> getCInstanceOf(fromVar, isType.rawType, toVar)
            }
        }

        override fun specify(typeArgs: Map<String, BsgType>): BsgType {
            return this
        }

        override fun equals(other: kotlin.Any?): Boolean {
            return other is Any || other is Generic && equals(other.rawType)
        }
    }

    data class Class(val name: String, val typeArgs: Map<String, BsgType>): BsgType() {
        override fun getCDefinitions(astMetadata: AstMetadata): List<String> {
            val builder = StringBuilder()
            builder.appendLine("struct BSG_Instance__$name {")
            builder.appendLine("struct BSG_AnyBaseInstance* baseInstance;")
            builder.appendLine("struct BSG_Class__${name}* class;")
            astMetadata.getClass(name).fields.forEach { (fieldName, fieldMeta) ->
                builder.appendLine("${fieldMeta.type.getCType()} $fieldName;");
            }
            builder.appendLine("};")
            builder.appendLine("typedef struct BSG_Instance__$name* BSG_InstancePtr__$name;")
            return listOf(builder.toString())
        }

        override fun getCTypeInternal(): String {
            return "BSG_InstancePtr__$name"
        }

        override fun writeCRetain(varName: String, writer: PicoWriter) {
            writer.writeln_r("if($varName) {")
            writer.writeln("$varName->baseInstance->baseClass->retain($varName->baseInstance);")
            writer.writeln_l("}")
        }

        override fun writeCRelease(varName: String, writer: PicoWriter) {
            writer.writeln_r("if($varName) {")
            writer.writeln("$varName->baseInstance->baseClass->release($varName->baseInstance);")
            writer.writeln_l("}")
        }

        override fun writeCCast(fromVar: String, toType: BsgType, toVar: String, ctx: ClassContext, cCurrentMethodWriter: PicoWriter, hNewMethodWriter: PicoWriter, cNewMethodWriter: PicoWriter) {
            when(toType) {
                Any -> {
                    cCurrentMethodWriter.writeln("${toType.getCType()} $toVar;")
                    cCurrentMethodWriter.writeln("$toVar.type = BSG_Any_ContentType__Instance;")
                    cCurrentMethodWriter.writeln("$toVar.content.instance = (BSG_AnyInstance*) $fromVar;")
                }
                is Class -> {
                    if(this == toType) {
                        cCurrentMethodWriter.writeln("${toType.getCType()} $toVar = $fromVar;")
                    } else {
                        cCurrentMethodWriter.writeln("${toType.getCType()} $toVar = (${toType.getCType()}) $fromVar->baseInstance->baseClass->cast($fromVar->baseInstance, BSG_Type__${toType.name});")
                    }
                }
                is Primitive -> error("Cannot cast from Class to Primitive.")
                is Method -> error("Cannot cast from Class to Method.")
                is Generic -> writeCCast(fromVar, toType.rawType, toVar, ctx, cCurrentMethodWriter, hNewMethodWriter, cNewMethodWriter)
            }
        }

        override fun getCInstanceOf(fromVar: String, isType: BsgType, toVar: String): String {
            return when(isType) {
                Any -> "BSG_Bool $toVar = true;"
                is Class -> "BSG_Bool $toVar = $fromVar->baseInstance->baseClass->canCast($fromVar->baseInstance, BSG_Type__${isType.name});"
                is Primitive -> error("Class type can never be a primitive.")
                is Method -> error("Class type can never be a method.")
                is Generic -> getCInstanceOf(fromVar, isType.rawType, toVar)
            }
        }

        override fun specify(typeArgs: Map<String, BsgType>): BsgType {
            return Class(name, this.typeArgs.mapValues { (_, t) -> t.specify(typeArgs) })
        }

        override fun equals(other: kotlin.Any?): Boolean {
            return other is Class && other.name == this.name && other.typeArgs == this.typeArgs ||
                    other is Generic && equals(other.rawType)
        }
    }

    data class Primitive(val name: String): BsgType() {
        override fun getCDefinitions(astMetadata: AstMetadata): List<String> {
            return emptyList()
        }

        override fun getCTypeInternal(): String {
            return "BSG_$name"
        }

        override fun writeCRetain(varName: String, writer: PicoWriter) {}

        override fun writeCRelease(varName: String, writer: PicoWriter) {}

        override fun writeCCast(fromVar: String, toType: BsgType, toVar: String, ctx: ClassContext, cCurrentMethodWriter: PicoWriter, hNewMethodWriter: PicoWriter, cNewMethodWriter: PicoWriter) {
            when(toType) {
                Any -> {
                    cCurrentMethodWriter.writeln("${toType.getCType()} $toVar;")
                    cCurrentMethodWriter.writeln("$toVar.type = BSG_Any_ContentType__${name};")
                    cCurrentMethodWriter.writeln("$toVar.content.primitive.${name}Value = $fromVar;")
                }
                is Class -> error("Cannot cast from Primitive to Class.")
                is Primitive -> cCurrentMethodWriter.writeln("${toType.getCType()} $toVar = (${getCType()}) $fromVar;")
                is Method -> error("Cannot cast from Primitive to Method.")
                is Generic -> writeCCast(fromVar, toType.rawType, toVar, ctx, cCurrentMethodWriter, hNewMethodWriter, cNewMethodWriter)
            }
        }

        override fun getCInstanceOf(fromVar: String, isType: BsgType, toVar: String): String {
            return when(isType) {
                Any -> "BSG_Bool $toVar = true;"
                is Class -> error("Primitive type can never be a class.")
                is Primitive -> "BSG_Bool $toVar = ${name == isType.name};"
                is Method -> error("Primitive type can never be a method.")
                is Generic -> getCInstanceOf(fromVar, isType.rawType, toVar)
            }
        }

        override fun specify(typeArgs: Map<String, BsgType>): BsgType {
            return this
        }
    }

    data class Method(val argTypes: List<BsgType>, val returnType: BsgType): BsgType() {
        private fun args() = argTypes.map { it.getCType() }
        private fun argsWithThis() = listOf("BSG_AnyInstance*") + args()

        fun getFName(): String {
            return "BSG_Function__｢${args().joinToString("·")}｣￫${returnType.getCType()}"
        }

        private fun getMName(): String {
            return "BSG_MethodFatPtr__｢${args().joinToString("·")}｣￫${returnType.getCType()}"
        }

        private fun getDName(): String {
            return "BSG_MethodDef__｢${args().joinToString("·")}｣￫${returnType.getCType()}"
        }

        override fun getCDefinitions(astMetadata: AstMetadata): List<String> {
            return argTypes.filterIsInstance<Method>().flatMap { it.getCDefinitions(astMetadata) } + listOf("""
                #ifndef ${getDName()}
                #define ${getDName()}
                typedef ${returnType.getCType()} (*${getFName()})(${argsWithThis().joinToString(",")});
                typedef struct ${getMName()} {
                    BSG_AnyInstance* this;
                    ${getFName()} method;
                } ${getMName()};
                typedef struct ${getMName()} ${getMName()};
                #endif
            """.trimIndent())
        }

        override fun getCTypeInternal(): String {
            return getMName()
        }

        override fun writeCRetain(varName: String, writer: PicoWriter) {
            writer.writeln_r("if($varName.this) {")
            writer.writeln("$varName.this->baseInstance->baseClass->retain($varName.this->baseInstance);")
            writer.writeln_l("}")
        }

        override fun writeCRelease(varName: String, writer: PicoWriter) {
            writer.writeln_r("if($varName.this) {")
            writer.writeln("$varName.this->baseInstance->baseClass->release($varName.this->baseInstance);")
            writer.writeln_l("}")
        }

        override fun writeCCast(fromVar: String, toType: BsgType, toVar: String, ctx: ClassContext, cCurrentMethodWriter: PicoWriter, hNewMethodWriter: PicoWriter, cNewMethodWriter: PicoWriter) {
            when(toType) {
                Any -> {
                    cCurrentMethodWriter.writeln("${toType.getCType()} $toVar;")
                    cCurrentMethodWriter.writeln("$toVar.type = BSG_Any_ContentType__Method;")
                    cCurrentMethodWriter.writeln("$toVar.content.method = (BSG_AnyMethodFatPtr) $fromVar;")
                }
                is Class -> error("Cannot cast from Method to Class.")
                is Primitive -> error("Cannot cast from Method to Primitive.")
                is Method -> {
                    if(argTypes.size != toType.argTypes.size) {
                        error("Method can only be cast to another method having the same number of parameters.")
                    }

                    // Save a space for recursive casts to be written.
                    val hNewMethodWriterBefore = hNewMethodWriter.createDeferredWriter()
                    val cNewMethodWriterBefore = cNewMethodWriter.createDeferredWriter()

                    // Make a typedef for the toType.
                    toType.getCDefinitions(ctx.astMetadata).forEach { ctx.hMethodTypedefs.writelnNotBlank(it) }

                    // Make a declaration for the method shim.
                    val shimName = "${ctx.className}_｢${args().joinToString("·")}｣￫${returnType.getCType()}_castTo_｢${toType.args().joinToString("·")}｣￫${toType.returnType.getCType()}"
                    hNewMethodWriter.writeln("#ifndef BSG_MethodCastShimDef_H__$shimName")
                    hNewMethodWriter.writeln("#define BSG_MethodCastShimDef_H__$shimName")
                    hNewMethodWriter.writeln("${toType.returnType.getCType()} BSG_MethodCastShim__$shimName(${toType.argsWithThis().joinToString(",")});")
                    hNewMethodWriter.writeln("#endif")

                    val castableArgNames = argTypes.map { ctx.getUniqueVarName() }
                    val argNames = listOf("this") + castableArgNames
                    val cArgTypesWithNames = toType.argsWithThis().mapIndexed { i, cType -> "$cType ${argNames[i]}" }.joinToString(",")

                    // Make a definition for the method shim.
                    cNewMethodWriter.writeln("#ifndef BSG_MethodCastShimDef_C__$shimName")
                    cNewMethodWriter.writeln("#define BSG_MethodCastShimDef_C__$shimName")
                    cNewMethodWriter.writeln_r("${toType.returnType.getCType()} BSG_MethodCastShim__$shimName($cArgTypesWithNames) {")
                    val castedNames = castableArgNames.mapIndexed { i, argName ->
                        // Cast args from the shim to the original to be able to call the original method.
                        val argType = toType.argTypes[i]
                        val argToType = argTypes[i]
                        // Write casted arg, and any new methods needed. Return new var.
                        val resultVar = ctx.getUniqueVarName()
                        argType.writeCCast(argName, argToType, resultVar, ctx, cNewMethodWriter, hNewMethodWriterBefore, cNewMethodWriterBefore)

                        // Retain casted methods. They will be freed by the accepting function.
                        if(argType is Method && argToType is Method) {
                            argToType.writeCRetain(resultVar, cNewMethodWriter)
                        }
                        resultVar
                    }
                    // Get original function from box.
                    cNewMethodWriter.writeln("${getFName()} originalMethod = (${getFName()}) ((BSG_Instance__BoxedMethod*)this)->method.method;")

                    // Call the original method.
                    val argsForOriginalMethod = (listOf("((BSG_Instance__BoxedMethod*)this)->method.this") + castedNames).joinToString(",")
                    // Retain original "this" instance before passing to method.
                    cNewMethodWriter.writeln("((BSG_Instance__BoxedMethod*)this)->method.this->baseInstance->baseClass->retain(((BSG_Instance__BoxedMethod*)this)->method.this->baseInstance);")
                    if(returnType is Primitive && returnType.name == "Void") {
                        cNewMethodWriter.writeln("originalMethod($argsForOriginalMethod);")
                        // Release box.
                        cNewMethodWriter.writeln("this->baseInstance->baseClass->release(this->baseInstance);")
                    } else {
                        val returnVar = ctx.getUniqueVarName()
                        val returnVarCasted = ctx.getUniqueVarName()
                        cNewMethodWriter.writeln("${returnType.getCType()} $returnVar = originalMethod($argsForOriginalMethod);")
                        // Release box.
                        cNewMethodWriter.writeln("this->baseInstance->baseClass->release(this->baseInstance);")

                        // Return types are retained.
                        returnType.writeCCast(returnVar, toType.returnType, returnVarCasted, ctx, cNewMethodWriter, hNewMethodWriterBefore, cNewMethodWriterBefore)
                        if(returnType is Method && toType.returnType is Method) {
                            toType.returnType.writeCRetain(returnVar, cNewMethodWriter)
                        }
                        cNewMethodWriter.writeln("return $returnVarCasted;")
                    }
                    cNewMethodWriter.writeln_l("}")
                    cNewMethodWriter.writeln("#endif")

                    // Put old instance and old method in a BoxedMethod instance.
                    val boxVar = ctx.getUniqueVarName()
                    cCurrentMethodWriter.writeln("BSG_Instance__BoxedMethod* $boxVar = BSG_Constructor__BoxedMethod();")
                    cCurrentMethodWriter.writeln("$boxVar->method.this = $fromVar.this;")
                    cCurrentMethodWriter.writeln("$boxVar->method.method = (BSG_AnyMethod)$fromVar.method;")
                    // Retain value saved to box.
                    cCurrentMethodWriter.writeln("$fromVar.this->baseInstance->baseClass->retain($fromVar.this->baseInstance);")

                    // Put box in new MethodFatPtr.
                    cCurrentMethodWriter.writeln("${toType.getCType()} $toVar;")
                    cCurrentMethodWriter.writeln("$toVar.this = (BSG_AnyInstance*)$boxVar;")
                    cCurrentMethodWriter.writeln("$toVar.method = &BSG_MethodCastShim__$shimName;")
                }
                is Generic -> writeCCast(fromVar, toType.rawType, toVar, ctx, cCurrentMethodWriter, hNewMethodWriter, cNewMethodWriter)
            }
        }

        override fun getCInstanceOf(fromVar: String, isType: BsgType, toVar: String): String {
            return when(isType) {
                Any -> "BSG_Bool $toVar = true;"
                is Class -> error("Method type can never be a class.")
                is Primitive -> error("Method type can never be a primitive.")
                is Method -> error("'MethodType is MethodType' is not yet supported.")
                is Generic -> getCInstanceOf(fromVar, isType.rawType, toVar)
            }
        }

        override fun specify(typeArgs: Map<String, BsgType>): BsgType {
            return Method(argTypes.map { it.specify(typeArgs) }, returnType.specify(typeArgs))
        }

        override fun equals(other: kotlin.Any?): Boolean {
            return other is Method && other.argTypes == this.argTypes && other.returnType == this.returnType ||
                    other is Generic && equals(other.rawType)
        }
    }

    data class Generic(val name: String, val rawType: BsgType): BsgType() {
        override fun getCDefinitions(astMetadata: AstMetadata): List<String> {
            return rawType.getCDefinitions(astMetadata)
        }

        override fun getCTypeInternal(): String {
            return rawType.getCType()
        }

        override fun writeCRetain(varName: String, writer: PicoWriter) {
            rawType.writeCRetain(varName, writer)
        }

        override fun writeCRelease(varName: String, writer: PicoWriter) {
            rawType.writeCRelease(varName, writer)
        }

        override fun writeCCast(fromVar: String, toType: BsgType, toVar: String, ctx: ClassContext, cCurrentMethodWriter: PicoWriter, hNewMethodWriter: PicoWriter, cNewMethodWriter: PicoWriter) {
            rawType.writeCCast(fromVar, toType, toVar, ctx, cCurrentMethodWriter, hNewMethodWriter, cNewMethodWriter)
        }

        override fun getCInstanceOf(fromVar: String, isType: BsgType, toVar: String): String {
            return rawType.getCInstanceOf(fromVar, isType, toVar)
        }

        override fun specify(typeArgs: Map<String, BsgType>): BsgType {
            return typeArgs[name] ?: this
        }

        override fun hashCode(): Int {
            return rawType.hashCode()
        }

        override fun equals(other: kotlin.Any?): Boolean {
            return rawType.equals(other)
        }
    }

    private var type: String? = null

    fun getCType(): String {
        val t = type
        if(t != null) {
            return t
        }
        return getCTypeInternal().also {
            type = it
        }
    }

    abstract fun getCDefinitions(astMetadata: AstMetadata): List<String>
    abstract fun getCTypeInternal(): String
    abstract fun writeCRetain(varName: String, writer: PicoWriter)
    abstract fun writeCRelease(varName: String, writer: PicoWriter)
    abstract fun writeCCast(fromVar: String, toType: BsgType, toVar: String, ctx: ClassContext, cCurrentMethodWriter: PicoWriter, hNewMethodWriter: PicoWriter, cNewMethodWriter: PicoWriter)
    abstract fun getCInstanceOf(fromVar: String, isType: BsgType, toVar: String): String
    abstract fun specify(typeArgs: Map<String, BsgType>): BsgType
}
