package io.deltawave.bsg.util

import io.deltawave.bsg.util.Either.Left
import io.deltawave.bsg.util.Either.Right
import org.jparsec.Parser
import org.jparsec.Parsers

sealed class Either<out A, out B> {
    data class Left<A>(val value: A): Either<A, Nothing>()
    data class Right<B>(val value: B): Either<Nothing, B>()
}

fun<A, B> either(a: Parser<A>, b: Parser<B>): Parser<Either<A, B>> {
    return Parsers.or(
        a.map<Either<A, B>>(::Left),
        b.map<Either<A, B>>(::Right)
    )
}