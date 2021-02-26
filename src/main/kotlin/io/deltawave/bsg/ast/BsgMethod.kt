package io.deltawave.bsg.ast

import io.deltawave.bsg.ast.type.BsgType
import io.deltawave.bsg.context.ClassContext
import io.deltawave.bsg.context.BlockScope

data class BsgMethod(
        val name: String,
        val arguments: List<Pair<String, BsgType>>,
        val returnType: BsgType,
        val body: BsgMethodBody,
        val attributes: Set<String>
) {
    fun toC(ctx: ClassContext, scope: BlockScope) {
        // Add all class/method args to lifetimes.
        val baseType = scope.getThisType()
        val methodOfType = ctx.astMetadata.getClass(baseType.name).methods[name]!!.methodOf

        val type = getType(ctx, baseType)

        val thisParam = ctx.getUniqueVarName()
        val thisArg = "BSG_AnyInstance* $thisParam"
        val otherArgs = arguments.map { (argName, type) -> "${type.getCType()} $argName" }
        val args = (listOf(thisArg) + otherArgs).joinToString(",")

        // Function declaration. - any instance as this param.
        ctx.hMethods.writeln("${type.returnType.getCType()} BSG_Method__${baseType.name}·$name($args);")

        // Function and body.
        ctx.cMethods.writeln_r("${type.returnType.getCType()} BSG_Method__${baseType.name}·$name($args) {")

        if(baseType.name != methodOfType.name) {
            ctx.cMethods.writeln("${baseType.getCType()} this = (${baseType.getCType()})$thisParam->baseInstance->baseClass->cast($thisParam->baseInstance, BSG_Type__${baseType.name});")
        } else {
            ctx.cMethods.writeln("${baseType.getCType()} this = (struct BSG_Instance__${baseType.name}*)$thisParam;")
        }

        scope.storeLifetimeAssociation("this", ctx.getUniqueLifetime(), scope.getThisType())
        arguments.filter { (_, argType) -> argType is BsgType.Class || argType is BsgType.Method || argType is BsgType.Any }
                .forEach { (argName, argType) ->
            scope.storeLifetimeAssociation(argName, ctx.getUniqueLifetime(), argType)
        }
        body.toC(ctx, scope)

        ctx.cMethods.writeln_l("}")
    }

    fun getType(ctx: ClassContext, thisType: BsgType.Class): BsgType.Method {
        return ctx.astMetadata.getClass(thisType.name).methods[name]!!.type as BsgType.Method
    }
}