package io.deltawave.bsg.context

import io.deltawave.bsg.ast.type.BsgType

sealed class VarMetadata(val varName: String, val type: BsgType) {
    class Method(
            varName: String,
            type: BsgType.Method,
            val methodOf: BsgType.Class,
            val args: List<Pair<String, BsgType>>,
            val attributes: Set<String>
    ): VarMetadata(varName, type)
    class Field(varName: String, type: BsgType, val fieldOf: BsgType.Class): VarMetadata(varName, type)
    class LocalOrGlobal(varName: String, type: BsgType): VarMetadata(varName, type)
}