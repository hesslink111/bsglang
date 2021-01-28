package io.deltawave.bsg.context

import io.deltawave.bsg.ast.type.BsgType

data class ClassMetadata(
        val type: BsgType.Class,
        val fields: Map<String, VarMetadata.Field>,
        val methods: Map<String, VarMetadata.Method>,
        val superTypes: Set<BsgType>,
        val attributes: Set<String>
) {
    val name: String get() = type.name
}