package io.deltawave.bsg.context

import io.deltawave.bsg.ast.type.BsgType

class ClassScope(private val classMetadata: ClassMetadata) {
    fun getVarMeta(varName: String): VarMetadata {
        return classMetadata.fields[varName]
                ?: classMetadata.methods[varName]
                ?: error("Could not find var in scope: $varName")
    }

    fun methodScope(methodMeta: VarMetadata.Method): MethodScope {
        return MethodScope(parentScope = this, thisVarType = classMetadata.type, methodMeta = methodMeta)
    }
}

class MethodScope(private val parentScope: ClassScope, val thisVarType: BsgType.Class, methodMeta: VarMetadata.Method) {
    private val currentScope = mutableMapOf<String, VarMetadata>()
    private val lifetimesByVar = mutableMapOf<String, Pair<Lifetime, BsgType>>()

    init {
        addVarMeta("this", thisVarType, fieldOf = null)
        methodMeta.args.forEach { (name, type) ->
            addVarMeta(name, type, fieldOf = null)
        }
    }

    fun getVarMeta(varName: String): VarMetadata {
        return currentScope[varName] ?: parentScope.getVarMeta(varName)
    }

    fun addVarMeta(varName: String, type: BsgType, fieldOf: BsgType.Class?) {
        currentScope[varName] = if(fieldOf != null) {
            VarMetadata.Field(varName, type, fieldOf)
        } else {
            VarMetadata.Local(varName, type)
        }
    }

    fun getThisType(): BsgType.Class {
        return thisVarType
    }

    fun storeLifetimeAssociation(varName: String, lifetime: Lifetime, type: BsgType) {
        lifetimesByVar[varName] = Pair(lifetime, type)
    }

    fun getLifetime(varName: String): Pair<Lifetime, BsgType>? {
        return lifetimesByVar[varName]
    }

    fun getAllLifetimes(): List<Lifetime> {
        return lifetimesByVar.values.map { (l, _) -> l }.distinct()
    }

    // Returns the first var. There will always be at least one
    // due to SAF.
    fun getVarForLifetime(lifetime: Lifetime): Pair<String, BsgType> {
        return lifetimesByVar.entries
                .first { (_, lt) -> lt.first == lifetime }
                .let { (varName, lt) -> Pair(varName, lt.second) }
    }
}