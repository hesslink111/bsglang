package io.deltawave.bsg.util

object Uncrustify {
    fun uncrustify(absolutePath: String) {
        ProcessBuilder("uncrustify", "-c", "-", "-f", absolutePath, "-o", absolutePath, "--no-backup")
                .start()
                .waitFor()
    }
}