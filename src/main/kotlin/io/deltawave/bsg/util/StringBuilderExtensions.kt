package io.deltawave.bsg.util

import org.ainslec.picocog.PicoWriter

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

fun PicoWriter.writelnNotBlank(string: String): PicoWriter {
    return when {
        string.isBlank() -> this
        string.endsWith('\n') -> writeln(string.dropLast(1)).let { this }
        else -> writeln(string)
    }
}
