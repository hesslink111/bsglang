package io.deltawave.bsg.context

sealed class LValueMetadata(val varName: String) {
    class Local(varName: String, val originalVarName: String): LValueMetadata(varName)
    class Field(varName: String): LValueMetadata(varName)
}