package io.deltawave.bsg.context

data class AstContext(
    val hFile: StringBuilder,
    val cFile: StringBuilder,
    val mainHFile: StringBuilder,
    val mainCFileInit: StringBuilder,
    val mainCFileMain: StringBuilder,
    val astMetadata: AstMetadata
) {
    private var nextTypeNum: Int = 0
    private var nextVarNum: Int = 0
    private var nextLifetime: Int = 0

    fun getNextTypeNum() = nextTypeNum++

    fun getUniqueVarName(): String {
        return "__${nextVarNum++}"
    }

    fun getUniqueLifetime(): Lifetime {
        return Lifetime(nextLifetime++)
    }
}