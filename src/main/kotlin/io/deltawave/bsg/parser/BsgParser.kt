package io.deltawave.bsg.parser

import io.deltawave.bsg.ast.*
import io.deltawave.bsg.ast.type.BsgType
import io.deltawave.bsg.util.Either
import io.deltawave.bsg.util.either
import org.jparsec.Parser
import org.jparsec.Parsers.sequence

object BsgParser {
    val methodBody: Parser<BsgMethodBody> = sequence(
        Tokens.openCurly.followedBy(Tokens.ws),
        StatementParser.statement
            .followedBy(Tokens.ws)
            .many()
            .followedBy(Tokens.closeCurly),
    ) { _, statements ->
        if(statements.lastOrNull() !is BsgStatement.Return && statements.lastOrNull() !is BsgStatement.EmptyReturn) {
            BsgMethodBody(statements + BsgStatement.EmptyReturn)
        } else {
            BsgMethodBody(statements)
        }
    }

    val method: Parser<BsgMethod> = sequence(
        AttributesParser.attributes.followedBy(Tokens.ws).optional(emptySet()),
        Tokens.identifier
            .followedBy(Tokens.ws)
            .followedBy(Tokens.openParen)
            .followedBy(Tokens.ws),
        sequence(
            StatementParser.fieldNameAndType.followedBy(Tokens.ws),
            StatementParser.fieldNameAndType.between(
                Tokens.comma.followedBy(Tokens.ws),
                Tokens.ws
            ).many()
        ) { ft, fts -> listOf(ft) + fts }
            .asOptional()
            .map { it.orElseGet { emptyList() } }
            .followedBy(Tokens.ws)
            .followedBy(Tokens.closeParen)
            .followedBy(Tokens.ws)
            .followedBy(Tokens.colon)
            .followedBy(Tokens.ws),
        TypeParser.type
            .followedBy(Tokens.ws),
        methodBody
            .followedBy(Tokens.ws),
    ) { attrs, methodName, arguments, returnType, methodBody -> BsgMethod(methodName, arguments, returnType, methodBody, attrs) }

    val classBody: Parser<BsgClassBody> = sequence(
        Tokens.openCurly.followedBy(Tokens.ws),
        either(StatementParser.field, method).followedBy(Tokens.ws).many()
            .followedBy(Tokens.ws)
            .followedBy(Tokens.closeCurly)
    ) { _, fieldOrMethods -> BsgClassBody(
        fieldOrMethods.filterIsInstance<Either.Left<BsgField>>().map { it.value },
        fieldOrMethods.filterIsInstance<Either.Right<BsgMethod>>().map { it.value }) }

    val classParser: Parser<BsgClass> = sequence(
        AttributesParser.attributes.followedBy(Tokens.ws).optional(emptySet()),
        Tokens.classKeyword.followedBy(Tokens.ws), // Class
        Tokens.identifier.followedBy(Tokens.ws), // ClassName
        sequence(
            Tokens.colon.followedBy(Tokens.ws),
            TypeParser.classType(TypeParser.type),
            sequence(
                    Tokens.comma.followedBy(Tokens.ws),
                    TypeParser.classType(TypeParser.type)
            ) { _, superClassName -> superClassName }.many()
        ) { _, superClassName, superClassNames -> listOf(superClassName) + superClassNames }
            .map { it.map { it as BsgType.Class } }
            .followedBy(Tokens.ws)
                .asOptional().map { it.orElseGet { emptyList() } },
        classBody
    ) { attrs, _, className, superClasses, body -> BsgClass(className, superClasses, body, attrs) }

    val file: Parser<BsgFile> = sequence(
        Tokens.ws,
        StatementParser.headerStatement
                .followedBy(Tokens.ws).many()
                .followedBy(Tokens.ws),
        classParser.followedBy(Tokens.ws)
    ) { _, hSources, cls -> BsgFile(hSources, cls) }
}
