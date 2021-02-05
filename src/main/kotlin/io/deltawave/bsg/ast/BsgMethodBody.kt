package io.deltawave.bsg.ast

import io.deltawave.bsg.context.AstContext
import io.deltawave.bsg.context.BlockScope

data class BsgMethodBody(val statements: List<BsgStatement>) {
    fun toC(ctx: AstContext, scope: BlockScope) {
        statements.forEach { it.toC(ctx, scope) }
    }
}