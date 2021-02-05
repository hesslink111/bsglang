package io.deltawave.bsg.ast

import io.deltawave.bsg.ast.type.BsgType
import io.deltawave.bsg.context.*

sealed class BsgStatement {
    data class If(val condition: BsgExpression, val statements: List<BsgStatement>): BsgStatement() {
        override fun toC(ctx: AstContext, scope: BlockScope) {
            val (conditionVarName, _) = condition.toC(ctx, scope)
            val conditionType = condition.getType(ctx, scope)
            assert(conditionType is BsgType.Primitive && conditionType.name == "Bool")

            ctx.cFile.appendLine("if($conditionVarName) {") // Begin if
            val subScope = scope.subScope()

            statements.forEach { it.toC(ctx, subScope) }

            releaseLifetimes(ctx, subScope, subScope.getAllLifetimes())
            ctx.cFile.appendLine("}") // End if
        }
    }

    data class While(val condition: BsgExpression, val statements: List<BsgStatement>): BsgStatement() {
        override fun toC(ctx: AstContext, scope: BlockScope) {
            ctx.cFile.appendLine("while(true) {") // Begin while
            val subScope = scope.subScope()

            val (conditionVarName, _) = condition.toC(ctx, subScope)
            val conditionType = condition.getType(ctx, subScope)
            assert(conditionType is BsgType.Primitive && conditionType.name == "Bool")

            ctx.cFile.appendLine("if(!$conditionVarName) {")
            ctx.cFile.appendLine("break;")
            ctx.cFile.appendLine("}")

            statements.forEach { it.toC(ctx, subScope) }

            releaseLifetimes(ctx, subScope, subScope.getAllLifetimes())
            ctx.cFile.appendLine("}") // End while
        }
    }

    data class Expression(val exp: BsgExpression): BsgStatement() {
        override fun toC(ctx: AstContext, scope: BlockScope) {
            exp.toC(ctx, scope)
        }
    }

    data class Assignment(val lValue: BsgLValueExpression, val rValue: BsgExpression): BsgStatement() {
        override fun toC(ctx: AstContext, scope: BlockScope) {
            assert(lValue.getType(ctx, scope) == rValue.getType(ctx, scope))

            val lValueMeta = lValue.toC(ctx, scope)
            val (rValueVar, rValueLifetime) = rValue.toC(ctx, scope)

            when (lValueMeta) {
                is LValueMetadata.Field -> { // Only retain/release if assigning to field.
                    if (rValue.getType(ctx, scope) is BsgType.Class) {
                        // Retain rValue if not null.
                        ctx.cFile.appendLine("if($rValueVar) {")
                        ctx.cFile.appendLine("$rValueVar->baseInstance->baseClass->retain($rValueVar->baseInstance);")
                        ctx.cFile.appendLine("}")
                        // Release lValue if not null.
                        ctx.cFile.appendLine("if(*${lValueMeta.varName}) {")
                        ctx.cFile.appendLine("(*${lValueMeta.varName})->baseInstance->baseClass->release((*${lValueMeta.varName})->baseInstance);")
                        ctx.cFile.appendLine("}")
                    } else if (rValue.getType(ctx, scope) is BsgType.Method) {
                        // Retain rValue if not null.
                        ctx.cFile.appendLine("if($rValueVar.this) {")
                        ctx.cFile.appendLine("$rValueVar.this->baseInstance->baseClass->retain($rValueVar.this->baseInstance);")
                        ctx.cFile.appendLine("}")
                        // Release lValue if not null.
                        ctx.cFile.appendLine("if((*${lValueMeta.varName}).this) {")
                        ctx.cFile.appendLine("(*${lValueMeta.varName}).this->baseInstance->baseClass->release((*${lValueMeta.varName}).this->baseInstance);")
                        ctx.cFile.appendLine("}")
                    } else if (rValue.getType(ctx, scope) is BsgType.Any) {
                        // Retain rValue if not null.
                        ctx.cFile.appendLine("if(!$rValueVar.isPrimitive && $rValueVar.instanceOrPrimitive.instance) {")
                        ctx.cFile.appendLine("$rValueVar.this->baseInstance->baseClass->retain($rValueVar.this->baseInstance);")
                        ctx.cFile.appendLine("}")
                        // Release lValue if not null.
                        ctx.cFile.appendLine("if((*${lValueMeta.varName}).isPrimitive && (*${lValueMeta.varName}).instanceOrPrimitive.instance) {")
                        ctx.cFile.appendLine("(*${lValueMeta.varName}).instanceOrPrimitive.instance->baseInstance->baseClass->release((*${lValueMeta.varName}).instanceOrPrimitive.instance->baseInstance);")
                        ctx.cFile.appendLine("}")
                    }
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
        override fun toC(ctx: AstContext, scope: BlockScope) {
            ctx.cFile.appendLine("${field.type.getCType()} ${field.name};")
            scope.addVarMeta(field.name, field.type, fieldOf = null)
        }
    }

    data class Return(val expression: BsgExpression): BsgStatement() {
        override fun toC(ctx: AstContext, scope: BlockScope) {
            val (expVar, expLifetime) = expression.toC(ctx, scope)
            releaseLifetimes(ctx, scope, scope.getAllLifetimes() - listOfNotNull(expLifetime))
            ctx.cFile.appendLine("return $expVar;")
        }
    }

    fun releaseLifetimes(ctx: AstContext, scope: BlockScope, lifetimes: List<Lifetime>) {
        lifetimes
                .map { scope.getVarForLifetime(it) }
                .forEach { (varName, varType) ->
                    if(varType is BsgType.Class) {
                        ctx.cFile.appendLine("if($varName) {")
                        ctx.cFile.appendLine("$varName->baseInstance->baseClass->release($varName->baseInstance);")
                        ctx.cFile.appendLine("}")
                    } else if(varType is BsgType.Method) {
                        ctx.cFile.appendLine("if($varName.this) {")
                        ctx.cFile.appendLine("$varName.this->baseInstance->baseClass->release($varName.this->baseInstance);")
                        ctx.cFile.appendLine("}")
                    }
                }
    }

    object EmptyReturn: BsgStatement() {
        override fun toC(ctx: AstContext, scope: BlockScope) {
            releaseLifetimes(ctx, scope, scope.getAllLifetimes())
            ctx.cFile.appendLine("return;")
        }
    }

    class CSource(val c: String): BsgStatement() {
        override fun toC(ctx: AstContext, scope: BlockScope) {
            ctx.cFile.appendLine(c)
        }
    }

    abstract fun toC(ctx: AstContext, scope: BlockScope)
}

sealed class BsgHeaderStatement {
    class HSource(val c: String): BsgHeaderStatement() {
        override fun toC(ctx: AstContext) {
            ctx.hFile.appendLine(c)
        }
    }

    class Import(val name: String): BsgHeaderStatement() {
        override fun toC(ctx: AstContext) {
            ctx.hFile.appendLine("#include \"$name.h\"")
        }
    }

    abstract fun toC(ctx: AstContext)
}