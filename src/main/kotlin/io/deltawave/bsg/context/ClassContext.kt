package io.deltawave.bsg.context

class ClassContext(
    val globalContext: GlobalContext,
    val hFile: StringBuilder,
    val cFile: StringBuilder,
    val mainHFile: StringBuilder,
    val mainCFileInit: StringBuilder,
    val mainCFileMain: StringBuilder,
    val mainCFileDeinit: StringBuilder,
    val astMetadata: AstMetadata
) {
    private var nextVarNum: Int = 0
    private var nextLifetime: Int = 0
    private val methodDefs = mutableSetOf<String>()

    fun getNextTypeNum() = globalContext.getNextTypeNum()

    fun getUniqueVarName(): String {
        return "_tmp_${nextVarNum++}"
    }

    fun getUniqueLifetime(): Lifetime {
        return Lifetime(nextLifetime++)
    }

    fun dedupMethodDef(def: String): String {
        return if(methodDefs.add(def)) {
            def
        } else {
            ""
        }
    }
}