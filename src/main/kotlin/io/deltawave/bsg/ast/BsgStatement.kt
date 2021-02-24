package io.deltawave.bsg.ast

import io.deltawave.bsg.ast.type.BsgType
import io.deltawave.bsg.context.*
import io.deltawave.bsg.util.writelnNotBlank

sealed class BsgStatement {
    data class If(val condition: BsgExpression, val statements: List<BsgStatement>): BsgStatement() {
        override fun toC(ctx: ClassContext, scope: BlockScope) {
            val (conditionVarName, _) = condition.toC(ctx, scope)
            val conditionType = condition.getType(ctx, scope)
            if(conditionType !is BsgType.Primitive || conditionType.name != "Bool") {
                error("If condition must be a boolean.")
            }

            ctx.cMethods.writeln_r("if($conditionVarName) {") // Begin if
            val subScope = scope.subScope()

            statements.forEach { it.toC(ctx, subScope) }

            releaseLifetimes(ctx, subScope, subScope.getAllLifetimesInBlock())
            ctx.cMethods.writeln_l("}") // End if
        }
    }

    data class While(val condition: BsgExpression, val statements: List<BsgStatement>): BsgStatement() {
        override fun toC(ctx: ClassContext, scope: BlockScope) {
            ctx.cMethods.writeln_r("while(true) {") // Begin while
            val subScope = scope.subScope()

            val (conditionVarName, _) = condition.toC(ctx, subScope)
            val conditionType = condition.getType(ctx, subScope)
            if(conditionType !is BsgType.Primitive || conditionType.name != "Bool") {
                error("While condition must be a boolean.")
            }

            ctx.cMethods.writeln_r("if(!$conditionVarName) {")
            ctx.cMethods.writeln("break;")
            ctx.cMethods.writeln_l("}")

            statements.forEach { it.toC(ctx, subScope) }

            releaseLifetimes(ctx, subScope, subScope.getAllLifetimesInBlock())
            ctx.cMethods.writeln_l("}") // End while
        }
    }

    data class Expression(val exp: BsgExpression): BsgStatement() {
        override fun toC(ctx: ClassContext, scope: BlockScope) {
            exp.toC(ctx, scope)
        }
    }

    data class Assignment(val lValue: BsgLValueExpression, val rValue: BsgExpression): BsgStatement() {
        override fun toC(ctx: ClassContext, scope: BlockScope) {
            val lValueType = lValue.getType(ctx, scope)
            val rValueType = rValue.getType(ctx, scope)
            if(lValueType != rValueType) {
                error("L-Value and R-Value must be equal: $lValueType, $rValueType")
            }

            val lValueMeta = lValue.toC(ctx, scope)
            val (rValueVar, rValueLifetime) = rValue.toC(ctx, scope)

            when (lValueMeta) {
                is LValueMetadata.Field -> { // Only retain/release if assigning to field.
                    rValueType.writeCRetain(rValueVar, ctx.cMethods)
                    rValueType.writeCRelease("(*${lValueMeta.varName})", ctx.cMethods)
                }
                is LValueMetadata.Local -> { // Local variable assignment.
                    // Replace var's current lifetime with new lifetime.
                    // This is purely so we know which lifetime is being returned.
                    if(rValueLifetime != null) {
                        scope.storeLifetimeAssociation(lValueMeta.originalVarName, rValueLifetime, lValue.getType(ctx, scope))
                    }
                }
            }

            ctx.cMethods.writeln("*${lValueMeta.varName} = $rValueVar;")
        }
    }
    data class Declaration(val field: BsgField): BsgStatement() {
        override fun toC(ctx: ClassContext, scope: BlockScope) {
            ctx.cMethods.writeln("${field.type.getCType()} ${field.name};")
            scope.addLocalVarMeta(field.name, field.type, fieldOf = null)
        }
    }

    data class MultipleStatements(val statements: List<BsgStatement>): BsgStatement() {
        override fun toC(ctx: ClassContext, scope: BlockScope) {
            statements.forEach { it.toC(ctx, scope) }
        }
    }

    data class Return(val expression: BsgExpression): BsgStatement() {
        override fun toC(ctx: ClassContext, scope: BlockScope) {
            val (expVar, expLifetime) = expression.toC(ctx, scope)
            releaseLifetimes(ctx, scope, scope.getAllLifetimesInScope() - listOfNotNull(expLifetime))
            ctx.cMethods.writeln("return $expVar;")
        }
    }

    object EmptyReturn: BsgStatement() {
        override fun toC(ctx: ClassContext, scope: BlockScope) {
            releaseLifetimes(ctx, scope, scope.getAllLifetimesInScope())
            ctx.cMethods.writeln("return;")
        }
    }

    class CSource(val c: String): BsgStatement() {
        override fun toC(ctx: ClassContext, scope: BlockScope) {
            ctx.cMethods.writeln(c)
        }
    }

    abstract fun toC(ctx: ClassContext, scope: BlockScope)
}

sealed class BsgHeaderStatement {
    class HSource(val c: String): BsgHeaderStatement() {
        override fun toC(ctx: ClassContext) {
            ctx.hIncludes.writeln(c)
        }
    }

    class Import(val name: String): BsgHeaderStatement() {
        override fun toC(ctx: ClassContext) {
            ctx.hIncludes.writeln("#include \"$name.h\"")
        }
    }

    abstract fun toC(ctx: ClassContext)
}