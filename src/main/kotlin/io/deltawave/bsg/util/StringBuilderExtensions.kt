package io.deltawave.bsg.util

fun StringBuilder.appendLineNotBlank(string: String): StringBuilder {
    return when {
        string.isBlank() -> this
        string.endsWith('\n') -> append(string)
        else -> appendLine(string)
    }
}

fun StringBuilder.appendLineNotBlank(stringBuilder: StringBuilder): StringBuilder {
    return when {
        stringBuilder.isBlank() -> this
        stringBuilder.endsWith('\n') -> append(stringBuilder)
        else -> appendLine(stringBuilder)
    }
}