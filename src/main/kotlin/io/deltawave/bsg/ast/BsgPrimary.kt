package io.deltawave.bsg.ast

import io.deltawave.bsg.ast.type.BsgType
import io.deltawave.bsg.context.ClassContext
import io.deltawave.bsg.context.BlockScope
import io.deltawave.bsg.context.VarLifetime
import io.deltawave.bsg.context.VarMetadata
import io.deltawave.bsg.util.writelnNotBlank

sealed class BsgPrimary {
    data class Var(val identifier: String, val typeArgs: Map<String, BsgType>): BsgPrimary() {
        override fun toC(ctx: ClassContext, scope: BlockScope): VarLifetime {
            val varMeta = scope.getVarMeta(identifier)
            return if(varMeta is VarMetadata.LocalOrGlobal) {
                if(typeArgs.isNotEmpty()) {
                    error("Type arguments can only be used on accessing class fields or methods. They may not be used on local or global variables.")
                }
                if(varMeta.isGlobal) {
                    // Global variable, does not have lifetime.
                    val resultVarName = ctx.getUniqueVarName()
                    ctx.cMethods.writeln("${varMeta.type.getCType()} $resultVarName;")
                    ctx.cMethods.writeln("$resultVarName = $identifier;")

                    // Retain
                    varMeta.type.writeCRetain(resultVarName, ctx.cMethods)
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
                methodOrFieldAccessToC(ctx, scope, VarLifetime("this", thisLifetime), thisType as BsgType.Class, identifier, typeArgs)
            }
        }

        override fun getType(ctx: ClassContext, scope: BlockScope): BsgType {
            return scope.getVarMeta(identifier).type
        }
    }

    data class Construction(val className: String, val typeArgs: Map<String, BsgType>): BsgPrimary() {
        override fun toC(ctx: ClassContext, scope: BlockScope): VarLifetime {
            val resultName = ctx.getUniqueVarName()
            ctx.cMethods.writeln("${getType(ctx, scope).getCType()} $resultName = BSG_Constructor__$className();")
            ctx.cMethods.writeln("$resultName->baseInstance->baseClass->retain($resultName->baseInstance);")
            val resultLifetime = ctx.getUniqueLifetime()
            scope.storeLifetimeAssociation(resultName, resultLifetime, getType(ctx, scope))
            return VarLifetime(resultName, resultLifetime)
        }

        override fun getType(ctx: ClassContext, scope: BlockScope): BsgType {
            return BsgType.Class(className, typeArgs)
        }
    }

    data class StringLiteral(val stringContents: String): BsgPrimary() {
        override fun toC(ctx: ClassContext, scope: BlockScope): VarLifetime {
            val resultName = ctx.getUniqueVarName()
            ctx.cMethods.writeln("${getType(ctx, scope).getCType()} $resultName = BSG_Constructor__String();")
            ctx.cMethods.writeln("$resultName->baseInstance->baseClass->retain($resultName->baseInstance);")
            ctx.cMethods.writeln("$resultName->cStr = \"$stringContents\";")
            ctx.cMethods.writeln("$resultName->length = ${stringContents.length};")
            ctx.cMethods.writeln("$resultName->isLiteral = true;")
            val resultLifetime = ctx.getUniqueLifetime()
            scope.storeLifetimeAssociation(resultName, resultLifetime, getType(ctx, scope))
            return VarLifetime(resultName, resultLifetime)
        }

        override fun getType(ctx: ClassContext, scope: BlockScope): BsgType {
            return BsgType.Class("String", emptyMap())
        }
    }

    data class CharLiteral(val charContents: String): BsgPrimary() {
        override fun toC(ctx: ClassContext, scope: BlockScope): VarLifetime {
            if(charContents.length != 1) {
                error("Char literal must contain a single character.")
            }
            val resultName = ctx.getUniqueVarName()
            ctx.cMethods.writeln("${getType(ctx, scope).getCType()} $resultName = '$charContents';")
            return VarLifetime(resultName, null)
        }

        override fun getType(ctx: ClassContext, scope: BlockScope): BsgType {
            return BsgType.Primitive("Char")
        }
    }

    data class BoolLiteral(val bool: String): BsgPrimary() {
        override fun toC(ctx: ClassContext, scope: BlockScope): VarLifetime {
            val resultName = ctx.getUniqueVarName()
            ctx.cMethods.writeln("BSG_Bool $resultName = $bool;")
            return VarLifetime(resultName, null)
        }

        override fun getType(ctx: ClassContext, scope: BlockScope): BsgType {
            return BsgType.Primitive("Bool")
        }
    }

    data class FloatLiteral(val float: String): BsgPrimary() {
        override fun toC(ctx: ClassContext, scope: BlockScope): VarLifetime {
            val resultName = ctx.getUniqueVarName()
            ctx.cMethods.writeln("BSG_Float $resultName = $float;")
            return VarLifetime(resultName, null)
        }

        override fun getType(ctx: ClassContext, scope: BlockScope): BsgType {
            return BsgType.Primitive("Float")
        }
    }

    data class IntLiteral(val int: String): BsgPrimary() {
        override fun toC(ctx: ClassContext, scope: BlockScope): VarLifetime {
            val resultName = ctx.getUniqueVarName()
            ctx.cMethods.writeln("BSG_Int $resultName = $int;")
            return VarLifetime(resultName, null)
        }

        override fun getType(ctx: ClassContext, scope: BlockScope): BsgType {
            return BsgType.Primitive("Int")
        }
    }

    abstract fun toC(ctx: ClassContext, scope: BlockScope): VarLifetime
    abstract fun getType(ctx: ClassContext, scope: BlockScope): BsgType
}