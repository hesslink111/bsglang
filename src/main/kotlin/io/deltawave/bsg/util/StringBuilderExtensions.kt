package io.deltawave.bsg.util

fun StringBuilder.appendLineNotBlank(string: String): StringBuilder {
    return if(string.isNotBlank()) {
        appendLine(string)
    } else {
        this
    }
}

fun StringBuilder.appendLineNotBlank(stringBuilder: StringBuilder): StringBuilder {
    return when {
        stringBuilder.isBlank() -> this
        stringBuilder.endsWith('\n') -> append(stringBuilder)
        else -> appendLine(stringBuilder)
    }
}