package io.deltawave.bsg.ast

import io.deltawave.bsg.ast.type.BsgType
import io.deltawave.bsg.context.*
import io.deltawave.bsg.util.appendLineNotBlank

sealed class BsgStatement {
    data class If(val condition: BsgExpression, val statements: List<BsgStatement>): BsgStatement() {
        override fun toC(ctx: ClassContext, scope: BlockScope) {
            val (conditionVarName, _) = condition.toC(ctx, scope)
            val conditionType = condition.getType(ctx, scope)
            assert(conditionType is BsgType.Primitive && conditionType.name == "Bool")

            ctx.cFile.appendLine("if($conditionVarName) {") // Begin if
            val subScope = scope.subScope()

            statements.forEach { it.toC(ctx, subScope) }

            releaseLifetimes(ctx, subScope, subScope.getAllLifetimesInBlock())
            ctx.cFile.appendLine("}") // End if
        }
    }

    data class While(val condition: BsgExpression, val statements: List<BsgStatement>): BsgStatement() {
        override fun toC(ctx: ClassContext, scope: BlockScope) {
            ctx.cFile.appendLine("while(true) {") // Begin while
            val subScope = scope.subScope()

            val (conditionVarName, _) = condition.toC(ctx, subScope)
            val conditionType = condition.getType(ctx, subScope)
            assert(conditionType is BsgType.Primitive && conditionType.name == "Bool")

            ctx.cFile.appendLine("if(!$conditionVarName) {")
            ctx.cFile.appendLine("break;")
            ctx.cFile.appendLine("}")

            statements.forEach { it.toC(ctx, subScope) }

            releaseLifetimes(ctx, subScope, subScope.getAllLifetimesInBlock())
            ctx.cFile.appendLine("}") // End while
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
            assert(lValueType == rValue.getType(ctx, scope))

            val lValueMeta = lValue.toC(ctx, scope)
            val (rValueVar, rValueLifetime) = rValue.toC(ctx, scope)

            when (lValueMeta) {
                is LValueMetadata.Field -> { // Only retain/release if assigning to field.
                    val rValueType = rValue.getType(ctx, scope)
                    ctx.cFile.appendLineNotBlank(rValueType.getCRetain(rValueVar))
                    ctx.cFile.appendLineNotBlank(rValueType.getCRelease("(*${lValueMeta.varName})"))
                }
                is LValueMetadata.Local -> { // Local variable assignment.
                    // Replace var's current lifetime with new lifetime.
                    // This is purely so we know which lifetime is being returned.
                    if(rValueLifetime != null) {
                        scope.storeLifetimeAssociation(lValueMeta.originalVarName, rValueLifetime, lValue.getType(ctx, scope))
                    }
                }
            }

            ctx.cFile.appendLine("*${lValueMeta.varName} = $rValueVar;")
        }
    }
    data class Declaration(val field: BsgField): BsgStatement() {
        override fun toC(ctx: ClassContext, scope: BlockScope) {
            ctx.cFile.appendLine("${field.type.getCType()} ${field.name};")
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
            ctx.cFile.appendLine("return $expVar;")
        }
    }

    object EmptyReturn: BsgStatement() {
        override fun toC(ctx: ClassContext, scope: BlockScope) {
            releaseLifetimes(ctx, scope, scope.getAllLifetimesInScope())
            ctx.cFile.appendLine("return;")
        }
    }

    class CSource(val c: String): BsgStatement() {
        override fun toC(ctx: ClassContext, scope: BlockScope) {
            ctx.cFile.appendLine(c)
        }
    }

    abstract fun toC(ctx: ClassContext, scope: BlockScope)
}

sealed class BsgHeaderStatement {
    class HSource(val c: String): BsgHeaderStatement() {
        override fun toC(ctx: ClassContext) {
            ctx.hFile.appendLine(c)
        }
    }

    class Import(val name: String): BsgHeaderStatement() {
        override fun toC(ctx: ClassContext) {
            ctx.hFile.appendLine("#include \"$name.h\"")
        }
    }

    abstract fun toC(ctx: ClassContext)
}