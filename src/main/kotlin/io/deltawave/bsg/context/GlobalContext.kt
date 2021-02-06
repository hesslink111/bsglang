package io.deltawave.bsg.context

class GlobalContext {
    private var nextTypeNum: Int = 0

    fun getNextTypeNum() = nextTypeNum++
}