package io.deltawave.bsg.context

data class Lifetime(val lifetimeNum: Int)

data class VarLifetime(val varName: String, val lifetime: Lifetime?)