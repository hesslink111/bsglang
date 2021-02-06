package io.deltawave.bsg.ast.type

sealed class BsgType {
    object Any: BsgType() {
        override fun getCTypeInternal(): String {
            return "struct BSG_Any"
        }

        override fun getCRetain(varName: String): String {
            return """
                if(!$varName.isPrimitive && $varName.instanceOrPrimitive.instance) {
                    $varName.instanceOrPrimitive.instance->baseInstance->baseClass->retain($varName.instanceOrPrimitive.instance->baseInstance);
                }
            """.trimIndent()
        }

        override fun getCRelease(varName: String): String {
            return """
                if(!$varName.isPrimitive && $varName.instanceOrPrimitive.instance) {
                    $varName.instanceOrPrimitive.instance->baseInstance->baseClass->release($varName.instanceOrPrimitive.instance->baseInstance);
                }
            """.trimIndent()
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
}
