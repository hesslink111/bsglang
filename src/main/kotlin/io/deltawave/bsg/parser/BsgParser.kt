package io.deltawave.bsg.parser

import io.deltawave.bsg.ast.*
import io.deltawave.bsg.util.Either
import io.deltawave.bsg.util.Either.Left
import io.deltawave.bsg.util.either
import io.deltawave.bsg.util.orNull
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
    ) { methodName, arguments, returnType, methodBody -> BsgMethod(methodName, arguments, returnType, methodBody) }

    val classBody: Parser<BsgClassBody> = sequence(
        Tokens.openCurly.followedBy(Tokens.ws),
        either(StatementParser.field, method).followedBy(Tokens.ws).many()
            .followedBy(Tokens.ws)
            .followedBy(Tokens.closeCurly)
    ) { _, fieldOrMethods -> BsgClassBody(
        fieldOrMethods.filterIsInstance<Either.Left<BsgField>>().map { it.value },
        fieldOrMethods.filterIsInstance<Either.Right<BsgMethod>>().map { it.value }) }

    val classParser: Parser<BsgClass> = sequence(
        Tokens.classKeyword.followedBy(Tokens.ws), // Class
        Tokens.identifier.followedBy(Tokens.ws), // ClassName
        sequence(
            Tokens.colon.followedBy(Tokens.ws),
            Tokens.identifier.followedBy(Tokens.ws),
            sequence(
                    Tokens.comma.followedBy(Tokens.ws),
                    Tokens.identifier
            ) { _, superClassName -> superClassName }.many()
        ) { _, superClassName, superClassNames -> listOf(superClassName) + superClassNames }
            .followedBy(Tokens.ws)
                .asOptional().map { it.orElseGet { emptyList() } },
        classBody
    ) { _, className, superClasses, body -> BsgClass(className, superClasses, body) }

    val file = sequence(
        Tokens.ws,
        classParser.followedBy(Tokens.ws)
    ) { _, c -> c }
}
