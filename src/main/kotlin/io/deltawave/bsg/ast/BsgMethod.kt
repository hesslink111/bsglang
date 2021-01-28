package io.deltawave.bsg.ast

import io.deltawave.bsg.ast.type.BsgType
import io.deltawave.bsg.context.AstContext
import io.deltawave.bsg.context.MethodScope

data class BsgMethod(
        val name: String,
        val arguments: List<Pair<String, BsgType>>,
        val returnType: BsgType,
        val body: BsgMethodBody,
        val attributes: Set<String>
) {
    fun toC(ctx: AstContext, scope: MethodScope) {
        // Add all class/method args to lifetimes.
        scope.storeLifetimeAssociation("this", ctx.getUniqueLifetime(), scope.getThisType())
        arguments.filter { (_, argType) -> argType is BsgType.Class || argType is BsgType.Method }
                .forEach { (argName, argType) ->
            scope.storeLifetimeAssociation(argName, ctx.getUniqueLifetime(), argType)
        }
        body.toC(ctx, scope)
    }
}