package io.deltawave.bsg.util

import org.jparsec.SourceLocation

fun parseError(sl: SourceLocation, message: String) {
    error("${sl.line}, ${sl.column}: $message")
}