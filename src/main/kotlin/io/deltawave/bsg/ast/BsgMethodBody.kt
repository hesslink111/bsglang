package io.deltawave.bsg.ast

import io.deltawave.bsg.context.AstContext
import io.deltawave.bsg.context.MethodScope

data class BsgMethodBody(val statements: List<BsgStatement>) {
    fun toC(ctx: AstContext, scope: MethodScope) {
        statements.forEach { it.toC(ctx, scope) }
    }
}