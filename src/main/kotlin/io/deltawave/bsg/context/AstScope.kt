package io.deltawave.bsg.context

import io.deltawave.bsg.ast.type.BsgType
import io.deltawave.bsg.util.appendLineNotBlank

interface Scope {
    fun getVarMeta(varName: String): VarMetadata
    fun getLifetime(varName: String): Pair<Lifetime, BsgType>?
    fun getVarForLifetime(lifetime: Lifetime): Pair<String, BsgType>
}

class GlobalScope(globalVarList: List<VarMetadata.LocalOrGlobal>): Scope {
    private val globalVars = globalVarList.associateBy { it.varName }
    override fun getVarMeta(varName: String): VarMetadata.LocalOrGlobal {
        return globalVars[varName] ?: error("Could not find var in scope: $varName")
    }

    override fun getLifetime(varName: String): Pair<Lifetime, BsgType>? {
        return null
    }

    override fun getVarForLifetime(lifetime: Lifetime): Pair<String, BsgType> {
        error("No lifetimes exist in global scope.")
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

    override fun getLifetime(varName: String): Pair<Lifetime, BsgType>? {
        return null
    }

    override fun getVarForLifetime(lifetime: Lifetime): Pair<String, BsgType> {
        error("No lifetimes exist in class scope.")
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

    override fun getLifetime(varName: String): Pair<Lifetime, BsgType>? {
        return lifetimesByVar[varName] ?: parentScope.getLifetime(varName)
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
    override fun getVarForLifetime(lifetime: Lifetime): Pair<String, BsgType> {
        return lifetimesByVar.entries
                .firstOrNull { (_, lt) -> lt.first == lifetime }
                ?.let { (varName, lt) -> Pair(varName, lt.second) }
                ?: parentScope.getVarForLifetime(lifetime)
    }

    fun subScope(): BlockScope {
        return BlockScope(this, thisVarType)
    }
}

fun releaseLifetimes(ctx: ClassContext, scope: BlockScope, lifetimes: List<Lifetime>) {
    lifetimes
            .map { scope.getVarForLifetime(it) }
            .forEach { (varName, varType) ->
                ctx.cFile.appendLineNotBlank(varType.getCRelease(varName))
            }
}