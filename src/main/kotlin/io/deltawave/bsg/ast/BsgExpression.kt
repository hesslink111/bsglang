package io.deltawave.bsg.ast

import io.deltawave.bsg.ast.type.BsgType
import io.deltawave.bsg.context.*

sealed class BsgExpression {
    data class Cast(val exp: BsgExpression, val toType: BsgType): BsgExpression() {
        override fun toC(ctx: AstContext, scope: BlockScope): VarLifetime {
            val (expVar, expLifetime) = exp.toC(ctx, scope)
            val expType = exp.getType(ctx, scope)
            val resultVar = ctx.getUniqueVarName()

            if(expType is BsgType.Class && toType is BsgType.Class) {
                // Class cast.
                ctx.cFile.appendLine("${toType.getCType()} $resultVar = $expVar->baseInstance->baseClass->cast($expVar->baseInstance, BSG_Type__${toType.name});")
            } else if(expType is BsgType.Class && toType is BsgType.Any) {
                // Class to Any.
                ctx.cFile.appendLine("struct BSG_Any $resultVar;")
                ctx.cFile.appendLine("$resultVar.isPrimitive = 0;")
                ctx.cFile.appendLine("$resultVar.instanceOrPrimitive.instance = (struct BSG_AnyInstance*) $expVar;")
            } else if(expType is BsgType.Method) {
                error("Method casting not yet supported.")
            } else if(expType is BsgType.Primitive && toType is BsgType.Any) {
                // Primitive to Any.
                ctx.cFile.appendLine("struct BSG_Any $resultVar;")
                ctx.cFile.appendLine("$resultVar.isPrimitive = 1;")
                ctx.cFile.appendLine("$resultVar.instanceOrPrimitive.primitive.${expType.name}Value = $expVar;")
            } else if(expType is BsgType.Primitive && toType is BsgType.Primitive) {
                // Primitive to Primitive
                ctx.cFile.appendLine("${toType.getCType()} $resultVar = (${toType.getCType()}) $expVar;")
            } else if(expType is BsgType.Any && toType is BsgType.Primitive) {
                // Any to Primitive
                ctx.cFile.appendLine("${toType.getCType()} $resultVar = $expVar.instanceOrPrimitive.primitive.${toType.name}Value;")
            } else if(expType is BsgType.Any && toType is BsgType.Class) {
                // Any to Class
                ctx.cFile.appendLine("${toType.getCType()} $resultVar = (${toType.getCType()}) $expVar.instanceOrPrimitive.instance;")
            } else {
                error("Cast not supported: from $expType to $toType")
            }
            return VarLifetime(resultVar, expLifetime)
        }

        override fun getType(ctx: AstContext, scope: BlockScope): BsgType {
            return toType
        }
    }

    data class Comparison(val e1: BsgExpression, val op: String, val e2: BsgExpression): BsgExpression() {
        override fun toC(ctx: AstContext, scope: BlockScope): VarLifetime {
            val (var1, _) = e1.toC(ctx, scope)
            val (var2, _) = e2.toC(ctx, scope)
            val u = ctx.getUniqueVarName()
            ctx.cFile.appendLine("${getType(ctx, scope).getCType()} $u = $var1 $op $var2;")
            return VarLifetime(u, null) // Only used for primitives.
        }

        override fun getType(ctx: AstContext, scope: BlockScope): BsgType {
            assert(e1.getType(ctx, scope) == e2.getType(ctx, scope))
            return e1.getType(ctx, scope)
        }
    }

    data class Add(val e1: BsgExpression, val op: String, val e2: BsgExpression): BsgExpression() {
        override fun toC(ctx: AstContext, scope: BlockScope): VarLifetime {
            val (var1, _) = e1.toC(ctx, scope)
            val (var2, _) = e2.toC(ctx, scope)
            val u = ctx.getUniqueVarName()
            ctx.cFile.appendLine("${getType(ctx, scope).getCType()} $u = $var1 $op $var2;")
            return VarLifetime(u, null) // Only used for primitives.
        }

        override fun getType(ctx: AstContext, scope: BlockScope): BsgType {
            assert(e1.getType(ctx, scope) == e2.getType(ctx, scope))
            return e1.getType(ctx, scope)
        }
    }

    data class Mul(val e1: BsgExpression, val op: String, val e2: BsgExpression): BsgExpression() {
        override fun toC(ctx: AstContext, scope: BlockScope): VarLifetime {
            val (var1, _) = e1.toC(ctx, scope)
            val (var2, _) = e2.toC(ctx, scope)
            val u = ctx.getUniqueVarName()
            ctx.cFile.appendLine("${getType(ctx, scope).getCType()} $u = $var1 $op $var2;")
            return VarLifetime(u, null) // Only used for primitives.
        }

        override fun getType(ctx: AstContext, scope: BlockScope): BsgType {
            assert(e1.getType(ctx, scope) == e2.getType(ctx, scope))
            return e1.getType(ctx, scope)
        }
    }

    data class Parenthetical(val exp: BsgExpression): BsgExpression() {
        override fun toC(ctx: AstContext, scope: BlockScope): VarLifetime {
            return exp.toC(ctx, scope)
        }

        override fun getType(ctx: AstContext, scope: BlockScope): BsgType = exp.getType(ctx, scope)
    }

    data class Primary(val primary: BsgPrimary): BsgExpression() {
        override fun toC(ctx: AstContext, scope: BlockScope): VarLifetime {
            return primary.toC(ctx, scope)
        }

        override fun getType(ctx: AstContext, scope: BlockScope) = primary.getType(ctx, scope)
    }

    // Writes to c file and returns variable name.
    abstract fun toC(ctx: AstContext, scope: BlockScope): VarLifetime
    abstract fun getType(ctx: AstContext, scope: BlockScope): BsgType
}

sealed class BsgLValueExpression {
    data class Access(val term: BsgExpression, val identifier: String): BsgLValueExpression() {
        override fun toC(ctx: AstContext, scope: BlockScope): LValueMetadata {
            val (termVar, _) = term.toC(ctx, scope) // Don't care what the lifetime was.
            val resultVar = ctx.getUniqueVarName()
            val resultType = getType(ctx, scope)

            ctx.cFile.appendLine("${resultType.getCType()}* $resultVar = &$termVar->$identifier;") // Pointer to field.
            return LValueMetadata.Field(resultVar)
        }

        override fun getType(ctx: AstContext, scope: BlockScope): BsgType {
            val termType = term.getType(ctx, scope) as? BsgType.Class ?: error("Cannot perform field access on non-class type.")
            val fieldMeta = ctx.astMetadata.getClass(termType.name).fields[identifier] ?: error("Cannot find field $identifier in type $termType.")
            return fieldMeta.type
        }
    }

    data class Var(val identifier: String): BsgLValueExpression() {
        override fun toC(ctx: AstContext, scope: BlockScope): LValueMetadata {
            val varMeta = scope.getVarMeta(identifier)
            val resultVar = ctx.getUniqueVarName()
            val resultType = getType(ctx, scope)

            ctx.cFile.appendLine("${resultType.getCType()}* $resultVar;")
            if(varMeta is VarMetadata.Field) {
                ctx.cFile.appendLine("$resultVar = &this->$identifier;")
            } else if(varMeta is VarMetadata.Method) {
                error("Cannot assign to method.")
            } else {
                ctx.cFile.appendLine("$resultVar = &$identifier;")
            }
            return LValueMetadata.Local(resultVar, identifier)
        }

        override fun getType(ctx: AstContext, scope: BlockScope): BsgType {
            return scope.getVarMeta(identifier).type
        }
    }

    abstract fun toC(ctx: AstContext, scope: BlockScope): LValueMetadata
    abstract fun getType(ctx: AstContext, scope: BlockScope): BsgType
}

data class BsgPostfixExpression(
    val exp: BsgExpression,
    val postfix: BsgPostfix
): BsgExpression() {
    override fun toC(ctx: AstContext, scope: BlockScope): VarLifetime {
        return postfix.toC(ctx, scope, exp)
    }

    override fun getType(ctx: AstContext, scope: BlockScope): BsgType {
        return postfix.getType(ctx, scope, exp)
    }
}

fun methodOrFieldAccessToC(ctx: AstContext, cls: ClassMetadata, instanceVar: String, identifier: String): String {
    val resultVarName = ctx.getUniqueVarName()
    when (identifier) {
        in cls.methods -> {
            val method = cls.methods[identifier]!!
            method.type as BsgType.Method
            ctx.cFile.appendLine("struct BSG_MethodFatPtr__${method.methodOf.name}_${method.varName} $resultVarName;")
            if(method.methodOf != cls.type) {
                ctx.cFile.appendLine("$resultVarName.this = $instanceVar->baseInstance->baseClass->cast($instanceVar->baseInstance, BSG_Type__${method.methodOf.name});")
            } else {
                ctx.cFile.appendLine("$resultVarName.this = $instanceVar;")
            }
            ctx.cFile.appendLine("$resultVarName.method = $instanceVar->class->$identifier;")
        }
        in cls.fields -> {
            val field = cls.fields[identifier]!!
            ctx.cFile.appendLine("${field.type.getCType()} $resultVarName = $instanceVar->$identifier;")
        }
        else -> error("No field or method in ${cls.type} named $identifier")
    }
    return resultVarName
}

sealed class BsgPostfix {
    data class Dot(val identifier: String): BsgPostfix() {
        override fun toC(ctx: AstContext, scope: BlockScope, exp: BsgExpression): VarLifetime {
            val (expVar, _) = exp.toC(ctx, scope)
            val expType = exp.getType(ctx, scope) as? BsgType.Class ?: error("Dot access can only be performed on class type.")

            val cls = ctx.astMetadata.getClass(expType.name)
            val resultVar = methodOrFieldAccessToC(ctx, cls, expVar, identifier)
            val resultType = getType(ctx, scope, exp)
            return if(resultType is BsgType.Class) {
                val resultLifetime = ctx.getUniqueLifetime()
                scope.storeLifetimeAssociation(resultVar, resultLifetime, getType(ctx, scope, exp))
                ctx.cFile.appendLine("$resultVar->baseInstance->baseClass->retain($resultVar->baseInstance);")
                VarLifetime(resultVar, resultLifetime)
            } else if(resultType is BsgType.Method) {
                val resultLifetime = ctx.getUniqueLifetime()
                scope.storeLifetimeAssociation(resultVar, resultLifetime, getType(ctx, scope, exp))
                ctx.cFile.appendLine("$resultVar.this->baseInstance->baseClass->retain($resultVar.this->baseInstance);")
                VarLifetime(resultVar, resultLifetime)
            } else if(resultType is BsgType.Any) {
                val resultLifetime = ctx.getUniqueLifetime()
                scope.storeLifetimeAssociation(resultVar, resultLifetime, getType(ctx, scope, exp))
                ctx.cFile.appendLine("if(!$resultVar.isPrimitive) {")
                ctx.cFile.appendLine("$resultVar.instanceOrPrimitive.instance->baseInstance->baseClass->retain($resultVar.instanceOrPrimitive.instance->baseInstance);")
                ctx.cFile.appendLine("}")
                VarLifetime(resultVar, resultLifetime)
            } else {
                VarLifetime(resultVar, null)
            }
        }

        override fun getType(ctx: AstContext, scope: BlockScope, exp: BsgExpression): BsgType {
            val expType = exp.getType(ctx, scope) as? BsgType.Class ?: error("Dot access can only be performed on class type.")
            return ctx.astMetadata.getClass(expType.name).let {
                it.methods[identifier]?.type ?: it.fields[identifier]?.type ?: error("No field or method in $expType named $identifier")
            }
        }
    }

    // Need to have the object whose method is being called.
    data class Call(val args: List<BsgExpression>): BsgPostfix() {
        override fun toC(ctx: AstContext, scope: BlockScope, exp: BsgExpression): VarLifetime {
            val (expVar, _) = exp.toC(ctx, scope) // Method Fat Pointer

            val argVarNames = args.map { it.toC(ctx, scope).varName }

            // Retain any class/method that gets passed as an argument, including this.
            ctx.cFile.appendLine("$expVar.this->baseInstance->baseClass->retain($expVar.this->baseInstance);")
            args.forEachIndexed { i, arg ->
                val argName = argVarNames[i]
                val argType = arg.getType(ctx, scope)
                if(argType is BsgType.Class) {
                    ctx.cFile.appendLine("$argName->baseInstance->baseClass->retain($argName->baseInstance);")
                } else if(argType is BsgType.Method) {
                    ctx.cFile.appendLine("$argName.this->baseInstance->baseClass->retain($argName.this->baseInstance);")
                } else if(argType is BsgType.Any) {
                    ctx.cFile.appendLine("if(!$argName.isPrimitive) {")
                    ctx.cFile.appendLine("$argName.instanceOrPrimitive.instance->baseInstance->baseClass->retain($argName.instanceOrPrimitive.instance->baseInstance);")
                    ctx.cFile.appendLine("}")
                }
            }

            val argVars = (listOf("$expVar.this") + argVarNames).joinToString(",")
            val resultVar = ctx.getUniqueVarName()
            val resultType = getType(ctx, scope, exp)
            if(resultType is BsgType.Primitive && resultType.name == "Void") {
                ctx.cFile.appendLine("$expVar.method($argVars);")
            } else {
                ctx.cFile.appendLine("${getType(ctx, scope, exp).getCType()} $resultVar = $expVar.method($argVars);")
            }
            return if(resultType.let { it is BsgType.Class || it is BsgType.Method || it is BsgType.Any }) {
                val resultLifetime = ctx.getUniqueLifetime()
                scope.storeLifetimeAssociation(resultVar, resultLifetime, getType(ctx, scope, exp))
                VarLifetime(resultVar, resultLifetime)
            } else {
                VarLifetime(resultVar, null)
            }
        }

        override fun getType(ctx: AstContext, scope: BlockScope, exp: BsgExpression): BsgType {
            val expType = exp.getType(ctx, scope) as? BsgType.Method ?: error("Can only perform call on method type.")
            return expType.returnType
        }
    }

    abstract fun toC(ctx: AstContext, scope: BlockScope, exp: BsgExpression): VarLifetime
    abstract fun getType(ctx: AstContext, scope: BlockScope, exp: BsgExpression): BsgType
}