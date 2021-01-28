package io.deltawave.bsg.ast

import io.deltawave.bsg.context.AstContext
import io.deltawave.bsg.context.GlobalScope

class BsgFile(val headerStatements: List<BsgHeaderStatement>, val cls: BsgClass) {
    fun toC(ctx: AstContext, globalScope: GlobalScope) {
        cls.toC(ctx, globalScope, headerStatements)
    }
}