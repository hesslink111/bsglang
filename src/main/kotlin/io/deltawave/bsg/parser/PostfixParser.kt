package io.deltawave.bsg.parser

import io.deltawave.bsg.ast.BsgExpression
import io.deltawave.bsg.ast.BsgPostfix
import io.deltawave.bsg.ast.BsgPostfixExpression
import io.deltawave.bsg.util.orNull
import org.jparsec.Parser
import org.jparsec.Parsers.or
import org.jparsec.Parsers.sequence

object PostfixParser {
    val dot: Parser<BsgPostfix> = sequence(
        Tokens.dot.followedBy(Tokens.ws),
        Tokens.identifier
    ) { _, id -> BsgPostfix.Dot(id) }

    fun callArgs(exp: Parser<BsgExpression>): Parser<List<BsgExpression>> = sequence(
        exp.followedBy(Tokens.ws),
        sequence(
            Tokens.comma.followedBy(Tokens.ws),
            exp.followedBy(Tokens.ws)
        ) { _, exp -> exp }.many()
    ) { e1, es -> listOf(e1) + es }
        .asOptional()
        .map { it.orNull() ?: emptyList() }

    fun call(exp: Parser<BsgExpression>): Parser<BsgPostfix> = sequence(
        Tokens.openParen.followedBy(Tokens.ws),
        callArgs(exp)
            .followedBy(Tokens.ws)
            .followedBy(Tokens.closeParen),
    ) { _, args -> BsgPostfix.Call(args) }

    fun postfix(exp: Parser<BsgExpression>): Parser<BsgPostfix> = or(
        dot,
        call(exp)
    )
}