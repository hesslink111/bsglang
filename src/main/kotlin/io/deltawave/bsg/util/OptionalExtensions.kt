package io.deltawave.bsg.util

import java.util.Optional

fun<T> Optional<T>.orNull(): T? = if (this.isPresent) this.get() else null