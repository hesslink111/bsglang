package io.deltawave.bsg.ast

import io.deltawave.bsg.ast.type.BsgType
import io.deltawave.bsg.context.ClassContext
import io.deltawave.bsg.context.BlockScope
import io.deltawave.bsg.context.VarLifetime
import io.deltawave.bsg.context.VarMetadata
import io.deltawave.bsg.util.appendLineNotBlank

sealed class BsgPrimary {
    data class Var(val identifier: String): BsgPrimary() {
        override fun toC(ctx: ClassContext, scope: BlockScope): VarLifetime {
            val varMeta = scope.getVarMeta(identifier)
            return if(varMeta is VarMetadata.LocalOrGlobal) {
                if(varMeta.isGlobal) {
                    // Global variable, does not have lifetime.
                    val resultVarName = ctx.getUniqueVarName()
                    ctx.cFile.appendLine("${varMeta.type.getCType()} $resultVarName;")
                    ctx.cFile.appendLine("$resultVarName = $identifier;")

                    // Retain
                    ctx.cFile.appendLineNotBlank(varMeta.type.getCRetain(resultVarName))
                    val resultLifetime = ctx.getUniqueLifetime()
                    scope.storeLifetimeAssociation(resultVarName, resultLifetime, varMeta.type)

                    VarLifetime(resultVarName, resultLifetime)
                } else {
                    // Local variable, already has lifetime.
                    val (resultLifetime, _) = scope.getLifetime(identifier) ?: Pair(null, null)
                    VarLifetime(identifier, resultLifetime)
                }
            } else {
                // "this" access.
                val (thisLifetime, thisType) = scope.getLifetime("this") ?: error("'this' must always be in scope.")
                val (thisVar, _) = scope.getVarForLifetime(thisLifetime)
                methodOrFieldAccessToC(ctx, scope, VarLifetime(thisVar, thisLifetime), thisType as BsgType.Class, identifier)
            }
        }

        override fun getType(ctx: ClassContext, scope: BlockScope): BsgType {
            return scope.getVarMeta(identifier).type
        }
    }

    data class Construction(val className: String): BsgPrimary() {
        override fun toC(ctx: ClassContext, scope: BlockScope): VarLifetime {
            val resultName = ctx.getUniqueVarName()
            ctx.cFile.appendLine("${getType(ctx, scope).getCType()} $resultName = BSG_Constructor__$className();")
            ctx.cFile.appendLine("$resultName->baseInstance->baseClass->retain($resultName->baseInstance);")
            val resultLifetime = ctx.getUniqueLifetime()
            scope.storeLifetimeAssociation(resultName, resultLifetime, getType(ctx, scope))
            return VarLifetime(resultName, resultLifetime)
        }

        override fun getType(ctx: ClassContext, scope: BlockScope): BsgType {
            return BsgType.Class(className)
        }
    }

    data class StringLiteral(val stringContents: String): BsgPrimary() {
        override fun toC(ctx: ClassContext, scope: BlockScope): VarLifetime {
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

        override fun getType(ctx: ClassContext, scope: BlockScope): BsgType {
            return BsgType.Class("String")
        }
    }

    data class FloatLiteral(val float: String): BsgPrimary() {
        override fun toC(ctx: ClassContext, scope: BlockScope): VarLifetime {
            val resultName = ctx.getUniqueVarName()
            ctx.cFile.appendLine("BSG_Float $resultName = $float;")
            return VarLifetime(resultName, null)
        }

        override fun getType(ctx: ClassContext, scope: BlockScope): BsgType {
            return BsgType.Primitive("Float")
        }
    }

    data class IntLiteral(val int: String): BsgPrimary() {
        override fun toC(ctx: ClassContext, scope: BlockScope): VarLifetime {
            val resultName = ctx.getUniqueVarName()
            ctx.cFile.appendLine("BSG_Int $resultName = $int;")
            return VarLifetime(resultName, null)
        }

        override fun getType(ctx: ClassContext, scope: BlockScope): BsgType {
            return BsgType.Primitive("Int")
        }
    }

    abstract fun toC(ctx: ClassContext, scope: BlockScope): VarLifetime
    abstract fun getType(ctx: ClassContext, scope: BlockScope): BsgType
}