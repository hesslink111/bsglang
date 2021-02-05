package io.deltawave.bsg.parser

import io.deltawave.bsg.ast.BsgField
import io.deltawave.bsg.ast.BsgHeaderStatement
import io.deltawave.bsg.ast.BsgStatement
import io.deltawave.bsg.ast.type.BsgType
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

    fun whileStatement(st: Parser<BsgStatement>): Parser<BsgStatement> = sequence(
            Tokens.whileKeyword
                    .followedBy(Tokens.ws)
                    .followedBy(Tokens.openParen)
                    .followedBy(Tokens.ws),
            ExpressionParser.expression
                    .followedBy(Tokens.ws)
                    .followedBy(Tokens.closeParen)
                    .followedBy(Tokens.ws)
                    .followedBy(Tokens.openCurly)
                    .followedBy(Tokens.ws),
            st.followedBy(Tokens.ws).many()
                    .followedBy(Tokens.ws)
                    .followedBy(Tokens.closeCurly),
    ) { _, e, sts -> BsgStatement.While(e, sts) }

    fun ifStatement(st: Parser<BsgStatement>): Parser<BsgStatement> = sequence(
            Tokens.ifKeyword
                    .followedBy(Tokens.ws)
                    .followedBy(Tokens.openParen)
                    .followedBy(Tokens.ws),
            ExpressionParser.expression
                    .followedBy(Tokens.ws)
                    .followedBy(Tokens.closeParen)
                    .followedBy(Tokens.ws)
                    .followedBy(Tokens.openCurly)
                    .followedBy(Tokens.ws),
            st.followedBy(Tokens.ws).many()
                    .followedBy(Tokens.ws)
                    .followedBy(Tokens.closeCurly),
    ) { _, e, sts -> BsgStatement.If(e, sts) }

    val expressionStatement: Parser<BsgStatement> = ExpressionParser.expression
            .followedBy(Tokens.ws)
            .followedBy(Tokens.semicolon)
            .map { BsgStatement.Expression(it) }

    val assignmentStatement: Parser<BsgStatement> = sequence(
        ExpressionParser.lValueExpression
            .followedBy(Tokens.ws)
            .followedBy(Tokens.equals)
            .followedBy(Tokens.ws),
        ExpressionParser.expression
            .followedBy(Tokens.ws)
            .followedBy(Tokens.semicolon)
    ) { lValue, rValue -> BsgStatement.Assignment(lValue, rValue) }

    val cSourceStatement: Parser<BsgStatement> = sequence(
        Tokens.cKeyword.followedBy(Tokens.ws),
        Tokens.stringLiteral
                .followedBy(Tokens.ws)
                .followedBy(Tokens.semicolon)
    ) { _, str -> BsgStatement.CSource(str) }

    val hSourceStatement: Parser<BsgHeaderStatement> = sequence(
            Tokens.hKeyword.followedBy(Tokens.ws),
            Tokens.stringLiteral
                    .followedBy(Tokens.ws)
                    .followedBy(Tokens.semicolon)
    ) { _, str -> BsgHeaderStatement.HSource(str) }

    val importStatement: Parser<BsgHeaderStatement> = sequence(
            Tokens.importKeyword.followedBy(Tokens.ws),
            Tokens.identifier
                    .followedBy(Tokens.ws)
                    .followedBy(Tokens.semicolon)
    ) { _, name -> BsgHeaderStatement.Import(name) }

    val headerStatement: Parser<BsgHeaderStatement> = or(
            hSourceStatement,
            importStatement
    )

    val statement: Parser<BsgStatement> by lazy {
        val stRef = Parser.newReference<BsgStatement>()
        val st = or(
                cSourceStatement,
                whileStatement(stRef.lazy()),
                ifStatement(stRef.lazy()),
                emptyReturnStatement,
                returnStatement,
                declarationStatement,
                expressionStatement,
                assignmentStatement
        )
        stRef.set(st)
        st
    }
}