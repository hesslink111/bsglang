package io.deltawave.bsg.ast.type

import io.deltawave.bsg.context.AstMetadata

sealed class BsgType {
    object Any: BsgType() {
        override fun getCDefinitions(astMetadata: AstMetadata): List<String> {
            return emptyList()
        }

        override fun getCTypeInternal(): String {
            return "BSG_Any"
        }

        override fun getCRetain(varName: String): String {
            return """
                if($varName.type == BSG_Any_ContentType__Instance && $varName.content.instance) {
                    $varName.content.instance->baseInstance->baseClass->retain($varName.content.instance->baseInstance);
                } else if($varName.type == BSG_Any_ContentType__Method && $varName.content.method.this) {
                    $varName.content.method.this->baseInstance->baseClass->retain($varName.content.method.this->baseInstance);
                }
            """.trimIndent()
        }

        override fun getCRelease(varName: String): String {
            return """
                if($varName.type == BSG_Any_ContentType__Instance && $varName.content.instance) {
                    $varName.content.instance->baseInstance->baseClass->release($varName.content.instance->baseInstance);
                } else if($varName.type == BSG_Any_ContentType__Method && $varName.content.method.this) {
                    $varName.content.method.this->baseInstance->baseClass->release($varName.content.method.this->baseInstance);
                }
            """.trimIndent()
        }

        override fun getCCast(fromVar: String, toType: BsgType, toVar: String): String {
            return when(toType) {
                Any -> error("Cast from Any to Any not supported.")
                is Class -> "${toType.getCType()} $toVar = (${toType.getCType()}) $fromVar.content.instance->baseInstance->baseClass->cast($fromVar.content.instance->baseInstance, BSG_Type__${toType.name});"
                is Primitive -> "${toType.getCType()} $toVar = $fromVar.content.primitive.${toType.name}Value;"
                is Method -> "${toType.getCType()} $toVar = (${toType.getCType()}) $fromVar.content.method;"
            }
        }

        override fun getCInstanceOf(fromVar: String, isType: BsgType, toVar: String): String {
            return when(isType) {
                Any -> "BSG_Bool $toVar = true;"
                is Class -> "BSG_Bool $toVar = $fromVar.type == BSG_Any_ContentType__Instance && $fromVar.content.instance->baseInstance->baseClass->canCast($fromVar.content.instance->baseInstance, BSG_Type__${isType.name});"
                is Primitive -> error("'Any is PrimitiveType' is not yet supported.")
                is Method -> error("'Any is MethodType' is not yet supported.")
            }
        }
    }

    data class Class(val name: String): BsgType() {
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

        override fun getCRetain(varName: String): String {
            return """
                if($varName) {
                    $varName->baseInstance->baseClass->retain($varName->baseInstance);
                }
            """.trimIndent()
        }

        override fun getCRelease(varName: String): String {
            return """
                if($varName) {
                    $varName->baseInstance->baseClass->release($varName->baseInstance);
                }
            """.trimIndent()
        }

        override fun getCCast(fromVar: String, toType: BsgType, toVar: String): String {
            return when(toType) {
                Any -> """
                    ${toType.getCType()} $toVar;
                    $toVar.type = BSG_Any_ContentType__Instance;
                    $toVar.content.instance = (BSG_AnyInstancePtr) $fromVar;
                """.trimIndent()
                is Class -> "${toType.getCType()} $toVar = (${toType.getCType()}) $fromVar->baseInstance->baseClass->cast($fromVar->baseInstance, BSG_Type__${toType.name});"
                is Primitive -> error("Cannot cast from Class to Primitive.")
                is Method -> error("Cannot cast from Class to Method.")
            }
        }

        override fun getCInstanceOf(fromVar: String, isType: BsgType, toVar: String): String {
            return when(isType) {
                Any -> "BSG_Bool $toVar = true;"
                is Class -> "BSG_Bool $toVar = $fromVar->baseInstance->baseClass->canCast($fromVar->baseInstance, BSG_Type__${isType.name});"
                is Primitive -> error("Class type can never be a primitive.")
                is Method -> error("Class type can never be a method.")
            }
        }
    }

    data class Primitive(val name: String): BsgType() {
        override fun getCDefinitions(astMetadata: AstMetadata): List<String> {
            return emptyList()
        }

        override fun getCTypeInternal(): String {
            return "BSG_$name"
        }

        override fun getCRetain(varName: String): String {
            return ""
        }

        override fun getCRelease(varName: String): String {
            return ""
        }

        override fun getCCast(fromVar: String, toType: BsgType, toVar: String): String {
            return when(toType) {
                Any -> """
                    ${toType.getCType()} $toVar;
                    $toVar.type = BSG_Any_ContentType__Primitive;
                    $toVar.content.primitive.${name}Value = $fromVar;
                """.trimIndent()
                is Class -> error("Cannot cast from Primitive to Class.")
                is Primitive -> "${toType.getCType()} $toVar = (${getCType()}) $fromVar;"
                is Method -> error("Cannot cast from Primitive to Method.")
            }
        }

        override fun getCInstanceOf(fromVar: String, isType: BsgType, toVar: String): String {
            return when(isType) {
                Any -> "BSG_Bool $toVar = true;"
                is Class -> error("Primitive type can never be a class.")
                is Primitive -> error("'PrimitiveType is PrimitiveType' is not yet supported.")
                is Method -> error("Primitive type can never be a method.")
            }
        }
    }

    data class Method(val argTypes: List<BsgType>, val returnType: BsgType): BsgType() {
        private fun args() = argTypes.map { it.getCType() }
        private fun argsWithThis() = listOf("BSG_AnyInstancePtr") + args()

        fun getFName(): String {
            return "BSG_Function__｢${argsWithThis().joinToString("·")}｣￫${returnType.getCType()}"
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
                    BSG_AnyInstancePtr this;
                    ${getFName()} method;
                } ${getMName()};
                typedef struct ${getMName()} ${getMName()};
                #endif
            """.trimIndent())
        }

        override fun getCTypeInternal(): String {
            return getMName()
        }

        override fun getCRetain(varName: String): String {
            return """
                if($varName.this) {
                    $varName.this->baseInstance->baseClass->retain($varName.this->baseInstance);
                }
            """.trimIndent()
        }

        override fun getCRelease(varName: String): String {
            return """
                if($varName.this) {
                    $varName.this->baseInstance->baseClass->release($varName.this->baseInstance);
                }
            """.trimIndent()
        }

        override fun getCCast(fromVar: String, toType: BsgType, toVar: String): String {
            return when(toType) {
                Any -> """
                    ${toType.getCType()} $toVar;
                    $toVar.type = BSG_Any_ContentType__Method;
                    $toVar.content.method = (BSG_AnyMethodFatPtr) $fromVar;
                """.trimIndent()
                is Class -> error("Cannot cast from Method to Class.")
                is Primitive -> error("Cannot cast from Method to Primitive.")
                is Method -> error("Cannot cast from Method to Method.")
            }
        }

        override fun getCInstanceOf(fromVar: String, isType: BsgType, toVar: String): String {
            return when(isType) {
                Any -> "BSG_Bool $toVar = true;"
                is Class -> error("Method type can never be a class.")
                is Primitive -> error("Method type can never be a primitive.")
                is Method -> error("'MethodType is MethodType' is not yet supported.")
            }
        }
    }

    // May return a typedeffed type. Typedef must have been emitted earlier.
    // TODO: All typedefs should be emitted earlier.
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
    abstract fun getCRetain(varName: String): String
    abstract fun getCRelease(varName: String): String
    abstract fun getCCast(fromVar: String, toType: BsgType, toVar: String): String
    abstract fun getCInstanceOf(fromVar: String, isType: BsgType, toVar: String): String
}
