package io.deltawave.bsg.context

import io.deltawave.bsg.ast.type.BsgType

interface Scope {
    fun getVarMeta(varName: String): VarMetadata
}

class GlobalScope(globalVarList: List<VarMetadata.LocalOrGlobal>): Scope {
    private val globalVars = globalVarList.associateBy { it.varName }
    override fun getVarMeta(varName: String): VarMetadata.LocalOrGlobal {
        return globalVars[varName] ?: error("Could not find var in scope: $varName")
    }
}

class ClassScope(private val globalScope: GlobalScope, private val classMetadata: ClassMetadata): Scope {
    override fun getVarMeta(varName: String): VarMetadata {
        return classMetadata.fields[varName]
                ?: classMetadata.methods[varName]
                ?: globalScope.getVarMeta(varName)
    }

    fun methodScope(methodMeta: VarMetadata.Method): BlockScope {
        val methodScope = BlockScope(parentScope = this, thisVarType = classMetadata.type)
        methodScope.addLocalVarMeta("this", classMetadata.type, fieldOf = null)
        methodMeta.args.forEach { (name, type) ->
            methodScope.addLocalVarMeta(name, type, fieldOf = null)
        }
        return methodScope
    }
}

class BlockScope(private val parentScope: Scope, val thisVarType: BsgType.Class): Scope {
    private val currentScope = mutableMapOf<String, VarMetadata>()
    private val lifetimesByVar = mutableMapOf<String, Pair<Lifetime, BsgType>>()

    override fun getVarMeta(varName: String): VarMetadata {
        return currentScope[varName] ?: parentScope.getVarMeta(varName)
    }

    fun addLocalVarMeta(varName: String, type: BsgType, fieldOf: BsgType.Class?) {
        currentScope[varName] = if(fieldOf != null) {
            VarMetadata.Field(varName, type, fieldOf)
        } else {
            VarMetadata.LocalOrGlobal(varName, type, isGlobal=false)
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

    fun getAllLifetimesInBlock(): List<Lifetime> {
        return lifetimesByVar.values.map { (l, _) -> l }.distinct()
    }

    fun getAllLifetimesInScope(): List<Lifetime> {
        val parentScopeLifetimes = if(parentScope is BlockScope) {
            parentScope.getAllLifetimesInScope()
        } else {
            emptyList()
        }
        return getAllLifetimesInBlock() + parentScopeLifetimes
    }

    // Returns the first var. There will always be at least one
    // due to SAF.
    fun getVarForLifetime(lifetime: Lifetime): Pair<String, BsgType> {
        return lifetimesByVar.entries
                .first { (_, lt) -> lt.first == lifetime }
                .let { (varName, lt) -> Pair(varName, lt.second) }
    }

    fun subScope(): BlockScope {
        return BlockScope(this, thisVarType)
    }
}