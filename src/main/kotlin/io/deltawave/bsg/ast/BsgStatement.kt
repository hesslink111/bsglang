package io.deltawave.bsg.ast

import io.deltawave.bsg.ast.type.BsgType
import io.deltawave.bsg.context.AstContext
import io.deltawave.bsg.context.LValueMetadata
import io.deltawave.bsg.context.MethodScope
import io.deltawave.bsg.context.VarMetadata

sealed class BsgStatement {
    data class Assignment(val lValue: BsgLValueExpression, val rValue: BsgExpression): BsgStatement() {
        override fun toC(ctx: AstContext, scope: MethodScope) {
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
                        ctx.cFile.appendLine("if(*${lValueMeta.varName}) {")
                        ctx.cFile.appendLine("(*${lValueMeta.varName}).this->baseInstance->baseClass->release((*${lValueMeta.varName}).this->baseInstance);")
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
        override fun toC(ctx: AstContext, scope: MethodScope) {
            ctx.cFile.appendLine("${field.type.getCType()} ${field.name};")
            if(field.type is BsgType.Class) {
                ctx.cFile.appendLine("${field.name};")
            } else if(field.type is BsgType.Method) {
                ctx.cFile.appendLine("${field.name}.this;")
            }
            scope.addVarMeta(field.name, field.type, fieldOf = null)
        }
    }

    data class Return(val expression: BsgExpression): BsgStatement() {
        override fun toC(ctx: AstContext, scope: MethodScope) {
            val (expVar, expLifetime) = expression.toC(ctx, scope)
            // Release anything constructed.
            (scope.getAllLifetimes() - listOfNotNull(expLifetime))
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

            ctx.cFile.appendLine("return $expVar;")
        }
    }

    object EmptyReturn: BsgStatement() {
        override fun toC(ctx: AstContext, scope: MethodScope) {
            // Release anything constructed.
            (scope.getAllLifetimes())
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

            ctx.cFile.appendLine("return;")
        }
    }

    abstract fun toC(ctx: AstContext, scope: MethodScope)
}