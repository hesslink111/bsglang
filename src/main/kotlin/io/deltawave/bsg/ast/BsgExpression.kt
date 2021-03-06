package io.deltawave.bsg.ast

import io.deltawave.bsg.ast.type.BsgType
import io.deltawave.bsg.context.*
import io.deltawave.bsg.util.parseError
import io.deltawave.bsg.util.writelnNotBlank
import org.jparsec.SourceLocation

sealed class BsgExpression {
    data class Cast(val exp: BsgExpression, val toType: BsgType): BsgExpression() {
        override fun toC(ctx: ClassContext, scope: BlockScope): VarLifetime {
            val (expVar, expLifetime) = exp.toC(ctx, scope)
            val expType = exp.getType(ctx, scope)

            val resultVar = ctx.getUniqueVarName()

            expType.writeCCast(expVar, toType, resultVar, ctx, ctx.cMethods, ctx.hNewMethodWriter, ctx.cNewMethodWriter)
            val resultLifetime = if(expType is BsgType.Method && toType is BsgType.Method) {
                // Retain boxed instance.
                toType.writeCRetain(resultVar, ctx.cMethods)
                // Must store new lifetime for casted method.
                val castedLifetime = ctx.getUniqueLifetime()
                scope.storeLifetimeAssociation(resultVar, castedLifetime, toType)
                castedLifetime
            } else {
                expLifetime
            }

            return VarLifetime(resultVar, resultLifetime)
        }

        override fun getType(ctx: ClassContext, scope: BlockScope): BsgType {
            return toType
        }
    }

    data class LogicalOperation(val e1: BsgExpression, val op: String, val e2: BsgExpression): BsgExpression() {
        override fun toC(ctx: ClassContext, scope: BlockScope): VarLifetime {
            val (var1, _) = e1.toC(ctx, scope)
            val resultVar = ctx.getUniqueVarName()

            // Lazily evaluate these expressions.
            ctx.cMethods.writeln("${getType(ctx, scope).getCType()} $resultVar;")
            if(op == "&&") {
                ctx.cMethods.writeln_r("if($var1) {")
                val subScope = scope.subScope()
                val (var2, _) = e2.toC(ctx, subScope)
                ctx.cMethods.writeln("$resultVar = $var2;")
                releaseLifetimes(ctx, subScope, subScope.getAllLifetimesInBlock())
                ctx.cMethods.writeln_lr("} else {")
                ctx.cMethods.writeln("$resultVar = false;")
                ctx.cMethods.writeln_l("}")
            } else if(op == "||") {
                ctx.cMethods.writeln_r("if($var1) {")
                ctx.cMethods.writeln("$resultVar = true;")
                ctx.cMethods.writeln_lr("} else {")
                val subScope = scope.subScope()
                val (var2, _) = e2.toC(ctx, subScope)
                ctx.cMethods.writeln("$resultVar = $var2;")
                releaseLifetimes(ctx, subScope, subScope.getAllLifetimesInBlock())
                ctx.cMethods.writeln_l("}")
            }

            return VarLifetime(resultVar, null)
        }

        override fun getType(ctx: ClassContext, scope: BlockScope): BsgType {
            return BsgType.Primitive("Bool")
        }
    }

    data class Equality(val e1: BsgExpression, val op: String, val e2: BsgExpression): BsgExpression() {
        override fun toC(ctx: ClassContext, scope: BlockScope): VarLifetime {
            val (var1, _) = e1.toC(ctx, scope)
            val (var2, _) = e2.toC(ctx, scope)
            val e1Type = e1.getType(ctx, scope)
            val u = ctx.getUniqueVarName()
            // TODO: Equality for different types:
            // TODO: - Equality and inequality function for Any type - switch case and stuff in preamble.c.
            fun write(type: BsgType) {
                when(type) {
                    BsgType.Any -> {
                        when(op) {
                            "==" -> ctx.cMethods.writeln("${getType(ctx, scope).getCType()} $u = BSG_Any_Equality($var1, $var2);")
                            "!=" -> ctx.cMethods.writeln("${getType(ctx, scope).getCType()} $u = BSG_Any_Equality($var1, $var2) == false;")
                            else -> error("Unknown equality operator: $op")
                        }
                    }
                    is BsgType.Class -> ctx.cMethods.writeln("${getType(ctx, scope).getCType()} $u = $var1 $op $var2;")
                    is BsgType.Primitive -> ctx.cMethods.writeln("${getType(ctx, scope).getCType()} $u = $var1 $op $var2;")
                    is BsgType.Method -> {
                        ctx.cMethods.writeln("${getType(ctx, scope).getCType()} $u = $var1.this $op $var2.this;")
                        ctx.cMethods.writeln("${getType(ctx, scope).getCType()} $u = $var1.method $op $var2.method;")
                    }
                    is BsgType.Generic -> write(type.rawType)
                }
            }
            write(e1Type)
            return VarLifetime(u, null) // Only used for primitives.
        }

        override fun getType(ctx: ClassContext, scope: BlockScope): BsgType {
            if(e1.getType(ctx, scope) != e2.getType(ctx, scope)) {
                error("Equality can only be performed on values of the same type.")
            }
            return BsgType.Primitive("Bool")
        }
    }

    data class InstanceOf(val e1: BsgExpression, val t: BsgType): BsgExpression() {
        override fun toC(ctx: ClassContext, scope: BlockScope): VarLifetime {
            val (var1, _) = e1.toC(ctx, scope)
            val var1Type = e1.getType(ctx, scope)
            val resultVar = ctx.getUniqueVarName()

            ctx.cMethods.writelnNotBlank(var1Type.getCInstanceOf(var1, t, resultVar))

            return VarLifetime(resultVar, null)
        }

        override fun getType(ctx: ClassContext, scope: BlockScope): BsgType {
            return BsgType.Primitive("Bool")
        }
    }

    data class Pipeline(val e1: BsgExpression, val e2: BsgExpression): BsgExpression() {
        override fun toC(ctx: ClassContext, scope: BlockScope): VarLifetime {
            val (e1Var, e1Lifetime) = e1.toC(ctx, scope)
            val e1Type = e1.getType(ctx, scope)

            val subScope = scope.subScope()
            subScope.addLocalVarMeta("it", e1Type, fieldOf = null)
            e1Lifetime?.let { subScope.storeLifetimeAssociation("it", it, e1Type) }

            val resultVar = ctx.getUniqueVarName()
            val resultType = e2.getType(ctx, subScope)

            ctx.cMethods.writeln("${resultType.getCType()} $resultVar;")
            ctx.cMethods.writeln_r("{")
            ctx.cMethods.writeln("${e1Type.getCType()} it = $e1Var;")
            val (e2Var, e2Lifetime) = e2.toC(ctx, subScope)
            ctx.cMethods.writeln("$resultVar = $e2Var;")

            releaseLifetimes(ctx, subScope, subScope.getAllLifetimesInBlock() - listOfNotNull(e2Lifetime))
            ctx.cMethods.writeln_l("}")

            return VarLifetime(resultVar, e2Lifetime)
        }

        override fun getType(ctx: ClassContext, scope: BlockScope): BsgType {
            val subScope = scope.subScope()
            subScope.addLocalVarMeta("it", e1.getType(ctx, scope), fieldOf = null)
            return e2.getType(ctx, subScope)
        }
    }

    data class Comparison(val e1: BsgExpression, val op: String, val e2: BsgExpression): BsgExpression() {
        override fun toC(ctx: ClassContext, scope: BlockScope): VarLifetime {
            val (var1, _) = e1.toC(ctx, scope)
            val (var2, _) = e2.toC(ctx, scope)
            val u = ctx.getUniqueVarName()
            ctx.cMethods.writeln("${getType(ctx, scope).getCType()} $u = $var1 $op $var2;")
            return VarLifetime(u, null)
        }

        override fun getType(ctx: ClassContext, scope: BlockScope): BsgType {
            if(e1.getType(ctx, scope) != e2.getType(ctx, scope)) {
                error("Comparison can only be performed on values of the same type.")
            }
            return BsgType.Primitive("Bool")
        }
    }

    data class Add(val e1: BsgExpression, val op: String, val e2: BsgExpression): BsgExpression() {
        override fun toC(ctx: ClassContext, scope: BlockScope): VarLifetime {
            val (var1, _) = e1.toC(ctx, scope)
            val (var2, _) = e2.toC(ctx, scope)
            val u = ctx.getUniqueVarName()
            ctx.cMethods.writeln("${getType(ctx, scope).getCType()} $u = $var1 $op $var2;")
            return VarLifetime(u, null) // Only used for primitives.
        }

        override fun getType(ctx: ClassContext, scope: BlockScope): BsgType {
            if(e1.getType(ctx, scope) != e2.getType(ctx, scope)) {
                error("Addition can only be performed on values of the same type.")
            }
            return e1.getType(ctx, scope)
        }
    }

    data class Mul(val e1: BsgExpression, val op: String, val e2: BsgExpression): BsgExpression() {
        override fun toC(ctx: ClassContext, scope: BlockScope): VarLifetime {
            val (var1, _) = e1.toC(ctx, scope)
            val (var2, _) = e2.toC(ctx, scope)
            val u = ctx.getUniqueVarName()
            ctx.cMethods.writeln("${getType(ctx, scope).getCType()} $u = $var1 $op $var2;")
            return VarLifetime(u, null) // Only used for primitives.
        }

        override fun getType(ctx: ClassContext, scope: BlockScope): BsgType {
            if(e1.getType(ctx, scope) != e2.getType(ctx, scope)) {
                error("Multiplication can only be performed on values of the same type.")
            }
            return e1.getType(ctx, scope)
        }
    }

    data class Parenthetical(val exp: BsgExpression): BsgExpression() {
        override fun toC(ctx: ClassContext, scope: BlockScope): VarLifetime {
            return exp.toC(ctx, scope)
        }

        override fun getType(ctx: ClassContext, scope: BlockScope): BsgType = exp.getType(ctx, scope)
    }

    data class Primary(val primary: BsgPrimary): BsgExpression() {
        override fun toC(ctx: ClassContext, scope: BlockScope): VarLifetime {
            return primary.toC(ctx, scope)
        }

        override fun getType(ctx: ClassContext, scope: BlockScope) = primary.getType(ctx, scope)
    }

    // Writes to c file and returns variable name.
    abstract fun toC(ctx: ClassContext, scope: BlockScope): VarLifetime
    abstract fun getType(ctx: ClassContext, scope: BlockScope): BsgType
}

sealed class BsgLValueExpression {
    data class Access(val term: BsgExpression, val identifier: String): BsgLValueExpression() {
        override fun toC(ctx: ClassContext, scope: BlockScope): LValueMetadata {
            val (termVar, _) = term.toC(ctx, scope) // Don't care what the lifetime was.
            val resultVar = ctx.getUniqueVarName()
            val resultType = getType(ctx, scope)

            ctx.cMethods.writeln("${resultType.getCType()}* $resultVar = &$termVar->$identifier;") // Pointer to field.
            return LValueMetadata.Field(resultVar)
        }

        override fun getType(ctx: ClassContext, scope: BlockScope): BsgType {
            val termType = term.getType(ctx, scope) as? BsgType.Class ?: error("Cannot perform field access on non-class type.")
            val fieldMeta = ctx.astMetadata.getClass(termType.name).fields[identifier] ?: error("Cannot find field $identifier in type $termType.")
            return fieldMeta.type
        }
    }

    data class Var(val identifier: String): BsgLValueExpression() {
        override fun toC(ctx: ClassContext, scope: BlockScope): LValueMetadata {
            val varMeta = scope.getVarMeta(identifier)
            val resultVar = ctx.getUniqueVarName()
            val resultType = getType(ctx, scope)

            ctx.cMethods.writeln("${resultType.getCType()}* $resultVar;")
            return when (varMeta) {
                is VarMetadata.Field -> {
                    ctx.cMethods.writeln("$resultVar = &this->$identifier;")
                    LValueMetadata.Field(resultVar)
                }
                is VarMetadata.Method -> error("Cannot assign to method.")
                else -> {
                    ctx.cMethods.writeln("$resultVar = &$identifier;")
                    LValueMetadata.Local(resultVar, identifier)
                }
            }
        }

        override fun getType(ctx: ClassContext, scope: BlockScope): BsgType {
            return scope.getVarMeta(identifier).type
        }
    }

    abstract fun toC(ctx: ClassContext, scope: BlockScope): LValueMetadata
    abstract fun getType(ctx: ClassContext, scope: BlockScope): BsgType
}

data class BsgPostfixExpression(
    val exp: BsgExpression,
    val postfix: BsgPostfix
): BsgExpression() {
    override fun toC(ctx: ClassContext, scope: BlockScope): VarLifetime {
        return postfix.toC(ctx, scope, exp)
    }

    override fun getType(ctx: ClassContext, scope: BlockScope): BsgType {
        return postfix.getType(ctx, scope, exp)
    }
}

fun methodOrFieldAccessToC(ctx: ClassContext, scope: BlockScope, instanceVarLifetime: VarLifetime, instanceType: BsgType.Class, identifier: String, typeArgs: Map<String, BsgType>): VarLifetime {
    val (instanceVar, instanceLifetime) = instanceVarLifetime
    val instanceMeta = ctx.astMetadata.getClass(instanceType.name)

    val (accessedVar, accessedLifetime, accessedType) = when (identifier) {
        in instanceMeta.methods -> {
            val methodVar = ctx.getUniqueVarName()
            val method = instanceMeta.methods[identifier]!!
            ctx.cMethods.writeln("${method.type.getCType()} $methodVar;")
            if(method.methodOf != instanceMeta.genericType) {
                ctx.cMethods.writeln("$methodVar.this = (BSG_AnyInstance*) $instanceVar->baseInstance->baseClass->cast($instanceVar->baseInstance, BSG_Type__${method.methodOf.name});")
            } else {
                ctx.cMethods.writeln("$methodVar.this = (BSG_AnyInstance*) $instanceVar;")
            }
            ctx.cMethods.writeln("$methodVar.method = $instanceVar->class->$identifier;")

            // Methods are already retained ("this" is already retained)
            Triple(methodVar, instanceLifetime, method.type)
        }
        in instanceMeta.fields -> {
            val fieldVar = ctx.getUniqueVarName()
            val field = instanceMeta.fields[identifier]!!
            ctx.cMethods.writeln("${field.type.getCType()} $fieldVar = $instanceVar->$identifier;")

            // Retain
            field.type.writeCRetain(fieldVar, ctx.cMethods)
            val resultLifetime = ctx.getUniqueLifetime()
            scope.storeLifetimeAssociation(fieldVar, resultLifetime, field.type)

            Triple(fieldVar, resultLifetime, field.type)
        }
        else -> error("No field or method in ${instanceMeta.genericType} named $identifier")
    }

    // Cast if generic.
    val specifiedType = accessedType.specify(instanceType.typeArgs + typeArgs)
    return if(specifiedType != accessedType) {
        val castedVar = ctx.getUniqueVarName()
        accessedType.writeCCast(accessedVar, specifiedType, castedVar, ctx, ctx.cMethods, ctx.hNewMethodWriter, ctx.cNewMethodWriter)
        val lifetime = if(accessedType is BsgType.Method && specifiedType is BsgType.Method) {
            // Retain boxed instance.
            specifiedType.writeCRetain(castedVar, ctx.cMethods)
            // Must store new lifetime for casted method.
            val castedLifetime = ctx.getUniqueLifetime()
            scope.storeLifetimeAssociation(castedVar, castedLifetime, specifiedType)
            castedLifetime
        } else {
            accessedLifetime
        }

        VarLifetime(castedVar, lifetime)
    } else {
        VarLifetime(accessedVar, accessedLifetime)
    }
}

sealed class BsgPostfix {
    data class Dot(val identifier: String, val typeArgs: Map<String, BsgType>): BsgPostfix() {
        override fun toC(ctx: ClassContext, scope: BlockScope, exp: BsgExpression): VarLifetime {
            val expVarLifetime = exp.toC(ctx, scope)
            val expType = exp.getType(ctx, scope) as? BsgType.Class ?: error("Cannot perform dot access on ${exp.getType(ctx, scope)}.")
            return methodOrFieldAccessToC(ctx, scope, expVarLifetime, expType, identifier, typeArgs)
        }

        override fun getType(ctx: ClassContext, scope: BlockScope, exp: BsgExpression): BsgType {
            val expType = exp.getType(ctx, scope) as? BsgType.Class ?: error("Dot access can only be performed on class type: ${exp.getType(ctx, scope)}")
            return ctx.astMetadata.getClass(expType.name).let {
                (it.methods[identifier]?.type ?: it.fields[identifier]?.type)?.specify(expType.typeArgs + typeArgs) ?: error("No field or method in $expType named $identifier")
            }
        }
    }

    // Need to have the object whose method is being called.
    data class Call(val args: List<BsgExpression>, val sl: SourceLocation): BsgPostfix() {
        override fun toC(ctx: ClassContext, scope: BlockScope, exp: BsgExpression): VarLifetime {
            val (expVar, _) = exp.toC(ctx, scope) // Method Fat Pointer
            val methodType = exp.getType(ctx, scope)
            if(methodType !is BsgType.Method || methodType.argTypes != args.map { it.getType(ctx, scope) }) {
                parseError(sl, "Call can only be performed on method where arg and parameter types match - method type: ${methodType}, call args: ${args.map { it.getType(ctx, scope) }}")
            }

            val argVarNames = args.map { it.toC(ctx, scope).varName }

            // Retain any class/method that gets passed as an argument, including this.
            ctx.cMethods.writeln("$expVar.this->baseInstance->baseClass->retain($expVar.this->baseInstance);")
            args.forEachIndexed { i, arg ->
                val argName = argVarNames[i]
                val argType = arg.getType(ctx, scope)
                argType.writeCRetain(argName, ctx.cMethods)
            }

            val argVars = (listOf("$expVar.this") + argVarNames).joinToString(",")
            val resultVar = ctx.getUniqueVarName()
            val resultType = getType(ctx, scope, exp)
            if(resultType is BsgType.Primitive && resultType.name == "Void") {
                ctx.cMethods.writeln("$expVar.method($argVars);")
            } else {
                ctx.cMethods.writeln("${getType(ctx, scope, exp).getCType()} $resultVar = $expVar.method($argVars);")
            }

            val resultLifetime = ctx.getUniqueLifetime()
            scope.storeLifetimeAssociation(resultVar, resultLifetime, getType(ctx, scope, exp))
            return VarLifetime(resultVar, resultLifetime)
        }

        override fun getType(ctx: ClassContext, scope: BlockScope, exp: BsgExpression): BsgType {
            val expType = exp.getType(ctx, scope) as? BsgType.Method ?: error("Can only perform call on method type.")
            return expType.returnType
        }
    }

    abstract fun toC(ctx: ClassContext, scope: BlockScope, exp: BsgExpression): VarLifetime
    abstract fun getType(ctx: ClassContext, scope: BlockScope, exp: BsgExpression): BsgType
}