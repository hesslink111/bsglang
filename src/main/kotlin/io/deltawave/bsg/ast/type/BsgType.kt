package io.deltawave.bsg.ast.type

import io.deltawave.bsg.context.AstContext

sealed class BsgType {
    data class Class(val name: String): BsgType() {
        override fun getCTypeInternal(builder: StringBuilder) {
            builder.append("struct BSG_Instance__$name*")
        }
    }

    data class Primitive(val name: String): BsgType() {
        override fun getCTypeInternal(builder: StringBuilder) {
            builder.append("BSG_$name")
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

        override fun getCTypeInternal(builder: StringBuilder) {
            builder.append(typeName ?: error("Must have emitted typedef already."))
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
        val builder = StringBuilder()
        getCTypeInternal(builder)
        return builder.toString().also {
            type = it
        }
    }

    abstract fun getCTypeInternal(builder: StringBuilder)
}
