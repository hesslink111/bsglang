package io.deltawave.bsg.ast.type

sealed class BsgType {
    object Any: BsgType() {
        override fun getCTypeInternal(): String {
            return "struct BSG_Any"
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
    }

    data class Class(val name: String): BsgType() {
        override fun getCTypeInternal(): String {
            return "struct BSG_Instance__$name*"
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
                    $toVar.content.instance = (struct BSG_AnyInstance*) $fromVar;
                """.trimIndent()
                is Class -> "${toType.getCType()} $toVar = (${toType.getCType()}) $fromVar->baseInstance->baseClass->cast($fromVar->baseInstance, BSG_Type__${toType.name});"
                is Primitive -> error("Cannot cast from Class to Primitive.")
                is Method -> error("Cannot cast from Class to Method.")
            }
        }
    }

    data class Primitive(val name: String): BsgType() {
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
    }

    data class Method(val argTypes: List<BsgType>, val returnType: BsgType): BsgType() {
        var typeName: String? = null

        fun getCTypedef(typeName: String): String {
            val args = argTypes
                    .mapIndexed { i, argType -> "${argType.getCType()} typedef_$i" }
                    .joinToString(",")
            return "typedef ${returnType.getCType()} (*$typeName)($args);"
        }

        override fun getCTypeInternal(): String {
            return typeName ?: error("Must have emitted typedef already.")
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

    abstract fun getCTypeInternal(): String

    abstract fun getCRetain(varName: String): String
    abstract fun getCRelease(varName: String): String
    abstract fun getCCast(fromVar: String, toType: BsgType, toVar: String): String
}
