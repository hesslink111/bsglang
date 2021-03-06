package io.deltawave.bsg.parser

import io.deltawave.bsg.ast.BsgExpression
import io.deltawave.bsg.ast.BsgPostfix
import io.deltawave.bsg.ast.BsgPostfixExpression
import io.deltawave.bsg.util.orNull
import org.jparsec.Parser
import org.jparsec.Parsers
import org.jparsec.Parsers.or
import org.jparsec.Parsers.sequence

object PostfixParser {
    val dot: Parser<BsgPostfix> = sequence(
        Tokens.dot.followedBy(Tokens.ws),
        Tokens.identifier.followedBy(Tokens.ws),
        TypeParser.typeArgs(TypeParser.type)
    ) { _, id, typeArgs -> BsgPostfix.Dot(id, typeArgs) }

    fun callArgs(exp: Parser<BsgExpression>): Parser<List<BsgExpression>> = sequence(
        exp.followedBy(Tokens.ws),
        sequence(
            Tokens.comma.followedBy(Tokens.ws),
            exp.followedBy(Tokens.ws)
        ) { _, e -> e }.many()
    ) { e1, es -> listOf(e1) + es }
        .asOptional()
        .map { it.orNull() ?: emptyList() }

    fun call(exp: Parser<BsgExpression>): Parser<BsgPostfix> = sequence(
        Parsers.SOURCE_LOCATION,
        Tokens.openParen.followedBy(Tokens.ws),
        callArgs(exp)
            .followedBy(Tokens.ws)
            .followedBy(Tokens.closeParen),
    ) { sl, _, args -> BsgPostfix.Call(args, sl) }

    fun postfix(exp: Parser<BsgExpression>): Parser<BsgPostfix> = or(
        dot,
        call(exp)
    )
}