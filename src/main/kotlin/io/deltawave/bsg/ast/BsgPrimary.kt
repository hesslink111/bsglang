package io.deltawave.bsg.ast

import io.deltawave.bsg.ast.type.BsgType
import io.deltawave.bsg.context.AstContext
import io.deltawave.bsg.context.BlockScope
import io.deltawave.bsg.context.VarLifetime
import io.deltawave.bsg.context.VarMetadata

sealed class BsgPrimary {
    data class Var(val identifier: String): BsgPrimary() {
        override fun toC(ctx: AstContext, scope: BlockScope): VarLifetime {
            val varMeta = scope.getVarMeta(identifier)
            return if(varMeta is VarMetadata.Field) {
                val thisType = ctx.astMetadata.getClass(varMeta.fieldOf.name)
                val resultVar = methodOrFieldAccessToC(ctx, thisType, "this", identifier)
                // Check the type of the field. The field can store a method also.
                if(varMeta.type is BsgType.Class || varMeta.type is BsgType.Method || varMeta.type is BsgType.Any) {
                    val resultLifetime = ctx.getUniqueLifetime()
                    scope.storeLifetimeAssociation(resultVar, resultLifetime, varMeta.type)
                    VarLifetime(resultVar, resultLifetime)
                } else {
                    VarLifetime(resultVar, null)
                }
            } else if(varMeta is VarMetadata.Method) {
                val thisType = ctx.astMetadata.getClass(varMeta.methodOf.name)
                val resultVar = methodOrFieldAccessToC(ctx, thisType, "this", identifier)
                val (resultLifetime, _) = scope.getLifetime("this") ?: error("'this' must have a lifetime.")
                VarLifetime(resultVar, resultLifetime)
                // Don't have to retain lifetime of "this", already retained.
                // Do have to retrieve lifetime for this.
            } else {
                // local field, already has lifetime.
                val (resultLifetime, _) = scope.getLifetime(identifier) ?: Pair(null, null)
                VarLifetime(identifier, resultLifetime)
            }
        }

        override fun getType(ctx: AstContext, scope: BlockScope): BsgType {
            return scope.getVarMeta(identifier).type
        }
    }

    data class Construction(val className: String): BsgPrimary() {
        override fun toC(ctx: AstContext, scope: BlockScope): VarLifetime {
            val resultName = ctx.getUniqueVarName()
            ctx.cFile.appendLine("${getType(ctx, scope).getCType()} $resultName = BSG_Constructor__$className();")
            ctx.cFile.appendLine("$resultName->baseInstance->baseClass->retain($resultName->baseInstance);")
            val resultLifetime = ctx.getUniqueLifetime()
            scope.storeLifetimeAssociation(resultName, resultLifetime, getType(ctx, scope))
            return VarLifetime(resultName, resultLifetime)
        }

        override fun getType(ctx: AstContext, scope: BlockScope): BsgType {
            return BsgType.Class(className)
        }
    }

    data class StringLiteral(val stringContents: String): BsgPrimary() {
        override fun toC(ctx: AstContext, scope: BlockScope): VarLifetime {
            val resultName = ctx.getUniqueVarName()
            val strVar = ctx.getUniqueVarName()
            ctx.cFile.appendLine("${getType(ctx, scope).getCType()} $resultName = BSG_Constructor__String();")
            ctx.cFile.appendLine("$resultName->baseInstance->baseClass->retain($resultName->baseInstance);")
            ctx.cFile.appendLine("char* $strVar = \"$stringContents\";")
            ctx.cFile.appendLine("$resultName->cStr = (BSG_Opaque) malloc(${stringContents.length + 1} * sizeof(char));")
            ctx.cFile.appendLine("strcpy((char*)$resultName->cStr, $strVar);")
            val resultLifetime = ctx.getUniqueLifetime()
            scope.storeLifetimeAssociation(resultName, resultLifetime, getType(ctx, scope))
            return VarLifetime(resultName, resultLifetime)
        }

        override fun getType(ctx: AstContext, scope: BlockScope): BsgType {
            return BsgType.Class("String")
        }
    }

    data class FloatLiteral(val float: String): BsgPrimary() {
        override fun toC(ctx: AstContext, scope: BlockScope): VarLifetime {
            val resultName = ctx.getUniqueVarName()
            ctx.cFile.appendLine("BSG_Float $resultName = $float;")
            return VarLifetime(resultName, null)
        }

        override fun getType(ctx: AstContext, scope: BlockScope): BsgType {
            return BsgType.Primitive("Float")
        }
    }

    data class IntLiteral(val int: String): BsgPrimary() {
        override fun toC(ctx: AstContext, scope: BlockScope): VarLifetime {
            val resultName = ctx.getUniqueVarName()
            ctx.cFile.appendLine("BSG_Int $resultName = $int;")
            return VarLifetime(resultName, null)
        }

        override fun getType(ctx: AstContext, scope: BlockScope): BsgType {
            return BsgType.Primitive("Int")
        }
    }

    abstract fun toC(ctx: AstContext, scope: BlockScope): VarLifetime
    abstract fun getType(ctx: AstContext, scope: BlockScope): BsgType
}