package io.deltawave.bsg.parser

import io.deltawave.bsg.ast.BsgField
import io.deltawave.bsg.ast.BsgStatement
import io.deltawave.bsg.ast.type.BsgType
import io.deltawave.bsg.util.either
import org.jparsec.Parser
import org.jparsec.Parsers.or
import org.jparsec.Parsers.sequence

object StatementParser {
    val fieldNameAndType: Parser<Pair<String, BsgType>> = sequence(
        Tokens.identifier
            .followedBy(Tokens.ws)
            .followedBy(Tokens.colon)
            .followedBy(Tokens.ws),
        TypeParser.type,
    ) { a, b -> Pair(a, b) }

    val field: Parser<BsgField> = sequence(
        Tokens.varKeyword.followedBy(Tokens.ws),
        fieldNameAndType
            .followedBy(Tokens.ws)
            .followedBy(Tokens.semicolon)
    ) { _, (fieldName, type) -> BsgField(fieldName, type) }

    val returnStatement: Parser<BsgStatement> = sequence(
        Tokens.returnKeyword.followedBy(Tokens.ws),
        ExpressionParser.expression
            .followedBy(Tokens.ws)
            .followedBy(Tokens.semicolon)
    ) { _, expression -> BsgStatement.Return(expression) }

    val emptyReturnStatement: Parser<BsgStatement> = Tokens.returnKeyword
            .followedBy(Tokens.ws)
            .followedBy(Tokens.semicolon)
            .map { BsgStatement.EmptyReturn }

    val declarationStatement: Parser<BsgStatement> = field.map { BsgStatement.Declaration(it) }

    val assignmentStatement: Parser<BsgStatement> = sequence(
        ExpressionParser.lValueExpression
            .followedBy(Tokens.ws)
            .followedBy(Tokens.equals)
            .followedBy(Tokens.ws),
        ExpressionParser.expression
            .followedBy(Tokens.ws)
            .followedBy(Tokens.semicolon)
    ) { lValue, rValue -> BsgStatement.Assignment(lValue, rValue) }

    val statement: Parser<BsgStatement> = or(
        emptyReturnStatement,
        returnStatement,
        declarationStatement,
        assignmentStatement
    )
}