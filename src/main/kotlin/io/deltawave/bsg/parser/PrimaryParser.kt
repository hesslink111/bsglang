package io.deltawave.bsg.parser

import io.deltawave.bsg.ast.BsgPrimary
import org.jparsec.Parser
import org.jparsec.Parsers
import org.jparsec.Parsers.or

object PrimaryParser {
    val identifier: Parser<BsgPrimary> = Tokens.identifier.map { BsgPrimary.Var(it) }
    val floatLiteral: Parser<BsgPrimary> = Tokens.floatLiteral.map { BsgPrimary.FloatLiteral(it) }
    val intLiteral: Parser<BsgPrimary> = Tokens.integerLiteral.map { BsgPrimary.IntLiteral(it) }
    val construction: Parser<BsgPrimary> = Parsers.sequence(
        Tokens.newKeyword.followedBy(Tokens.ws),
        Tokens.identifier
            .followedBy(Tokens.ws)
            .followedBy(Tokens.openParen)
            .followedBy(Tokens.ws)
            .followedBy(Tokens.closeParen),
    ) { _, constructorName -> BsgPrimary.Construction(constructorName) }
    val stringLiteral: Parser<BsgPrimary> = Tokens.stringLiteral.map { BsgPrimary.StringLiteral(it) }

    val primary: Parser<BsgPrimary> = or(
        construction,
        identifier,
        stringLiteral,
        floatLiteral,
        intLiteral
    )
}