package io.deltawave.bsg.output

class CBlockBuilder(val name: String) {
    private val statements = mutableListOf<String>()
    private val deferredStatements = mutableListOf<String>()

    fun addStatement(c: String) {
        statements.add(c)
    }

    fun addDeferred(c: String) {
        deferredStatements.add(c)
    }

    fun toC(): String {
        // Deferred statements are run after any return statement.
        TODO()
    }
}