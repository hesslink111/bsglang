package io.deltawave.bsg.parser

import io.deltawave.bsg.ast.BsgPrimary
import org.jparsec.Parser
import org.jparsec.Parsers
import org.jparsec.Parsers.or
import org.jparsec.Parsers.sequence

object PrimaryParser {
    val boolLiteral: Parser<BsgPrimary> = Tokens.boolLiteral.map { BsgPrimary.BoolLiteral(it) }
    val namedVar: Parser<BsgPrimary> = sequence(
        Tokens.identifier.followedBy(Tokens.ws),
        TypeParser.typeArgs(TypeParser.type)
    ) { id, typeArgs -> BsgPrimary.Var(id, typeArgs) }
    val floatLiteral: Parser<BsgPrimary> = Tokens.floatLiteral.map { BsgPrimary.FloatLiteral(it) }
    val intLiteral: Parser<BsgPrimary> = Tokens.integerLiteral.map { BsgPrimary.IntLiteral(it) }
    val construction: Parser<BsgPrimary> = sequence(
        Tokens.newKeyword.followedBy(Tokens.ws),
        Tokens.identifier
            .followedBy(Tokens.ws),
        TypeParser.typeArgs(TypeParser.type)
            .followedBy(Tokens.openParen)
            .followedBy(Tokens.ws)
            .followedBy(Tokens.closeParen),
    ) { _, constructorName, typeArgs -> BsgPrimary.Construction(constructorName, typeArgs) }
    val stringLiteral: Parser<BsgPrimary> = Tokens.doubleQStringLiteral.map { BsgPrimary.StringLiteral(it) }
    val charLiteral: Parser<BsgPrimary> = Tokens.singleQStringLiteral.map { BsgPrimary.CharLiteral(it) }

    val primary: Parser<BsgPrimary> = or(
        construction,
        boolLiteral,
        namedVar,
        stringLiteral,
    charLiteral,
        floatLiteral,
        intLiteral
    )
}