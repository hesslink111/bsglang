package io.deltawave.bsg.parser

import io.deltawave.bsg.ast.*
import org.jparsec.Parser
import org.jparsec.Parsers.or
import org.jparsec.Parsers.sequence

object ExpressionParser {
    val primary: Parser<BsgExpression> = PrimaryParser.primary.map { p -> BsgExpression.Primary(p) }

    fun parenthetical(exp: Parser<BsgExpression>): Parser<BsgExpression> = sequence(
        Tokens.openParen.followedBy(Tokens.ws),
        exp.followedBy(Tokens.ws).followedBy(Tokens.closeParen)
    ) { _, e -> BsgExpression.Parenthetical(e) }

    fun term(exp: Parser<BsgExpression>): Parser<BsgExpression> = or(
        parenthetical(exp),
        primary
    )

    fun access(exp: Parser<BsgExpression>): Parser<BsgExpression> = sequence(
            term(exp).followedBy(Tokens.ws),
            PostfixParser.postfix(exp).followedBy(Tokens.ws).many()
    ) { term, accesses -> if(accesses.isEmpty()) term else {
        accesses.fold(term) { t, postfix -> BsgPostfixExpression(t, postfix) }
    } }

    fun cast(exp: Parser<BsgExpression>): Parser<BsgExpression> = sequence(
            access(exp).followedBy(Tokens.ws),
            sequence(
                    Tokens.asKeyword.followedBy(Tokens.ws),
                    TypeParser.type
            ).asOptional()
    ) { acc, t -> if(t.isPresent) {
        BsgExpression.Cast(acc, t.get())
    } else {
        acc
    } }

    fun mul(exp: Parser<BsgExpression>): Parser<BsgExpression> = sequence(
        cast(exp).followedBy(Tokens.ws),
        sequence(
            Tokens.ws,
            or(Tokens.mul, Tokens.div, Tokens.rem).source().followedBy(Tokens.ws),
            cast(exp),
        ) { _, op, e -> Pair(op, e) }
            .asOptional()
    ) { ca, addExp ->
        if(addExp.isPresent)
            BsgExpression.Mul(ca, addExp.get().first, addExp.get().second)
        else
            ca
    }

    fun add(exp: Parser<BsgExpression>): Parser<BsgExpression> = sequence(
        mul(exp)
            .followedBy(Tokens.ws),
        sequence(
            Tokens.ws,
            or(Tokens.plus, Tokens.minus).source().followedBy(Tokens.ws),
            mul(exp)
        ) { _, op, e -> Pair(op, e) }
            .asOptional()
    ) { mul, addExp ->
        if(addExp.isPresent)
            BsgExpression.Add(mul, addExp.get().first, addExp.get().second)
        else
            mul
    }

    fun instanceOf(exp: Parser<BsgExpression>): Parser<BsgExpression> = sequence(
            add(exp).followedBy(Tokens.ws),
            sequence(
                    Tokens.ws
                            .followedBy(Tokens.isKeyword)
                            .followedBy(Tokens.ws),
                    TypeParser.type
            ) { _, t -> t }
                    .asOptional()
    ) { a, instExp -> instExp.map<BsgExpression>{ t -> BsgExpression.InstanceOf(a, t) }.orElseGet { a } }

    fun comparison(exp: Parser<BsgExpression>): Parser<BsgExpression> = sequence(
        instanceOf(exp).followedBy(Tokens.ws),
        sequence(
            Tokens.ws,
            or(Tokens.gte, Tokens.gt, Tokens.lte, Tokens.lt).source().followedBy(Tokens.ws),
            instanceOf(exp)
        ) { _, op, e -> Pair(op, e) }
            .asOptional()
    ) { a, compExp -> compExp.map<BsgExpression>{ (op, e) -> BsgExpression.Comparison(a, op, e) }.orElseGet { a } }

    fun equality(exp: Parser<BsgExpression>): Parser<BsgExpression> = sequence(
            comparison(exp).followedBy(Tokens.ws),
            sequence(
                    Tokens.ws,
                    Tokens.equality.source().followedBy(Tokens.ws),
                    comparison(exp)
            ) { _, op, e -> Pair(op, e) }
                    .asOptional()
    ) { a, eqExp -> eqExp.map<BsgExpression>{ (op, e) -> BsgExpression.Equality(a, op, e) }.orElseGet { a } }

    fun logicalAnd(exp: Parser<BsgExpression>): Parser<BsgExpression> = sequence(
            equality(exp).followedBy(Tokens.ws),
            sequence(
                    Tokens.ws,
                    Tokens.logicalAnd.source().followedBy(Tokens.ws),
                    equality(exp)
            ) { _, op, e -> Pair(op, e) }
                    .asOptional()
    ) { a, logicExp -> logicExp.map<BsgExpression>{ (op, e) -> BsgExpression.LogicalOperation(a, op, e) }.orElseGet { a } }

    fun logicalOr(exp: Parser<BsgExpression>): Parser<BsgExpression> = sequence(
            logicalAnd(exp).followedBy(Tokens.ws),
            sequence(
                    Tokens.ws,
                    Tokens.logicalOr.source().followedBy(Tokens.ws),
                    logicalAnd(exp)
            ) { _, op, e -> Pair(op, e) }
                    .asOptional()
    ) { a, logicExp -> logicExp.map<BsgExpression>{ (op, e) -> BsgExpression.LogicalOperation(a, op, e) }.orElseGet { a } }

    val expression: Parser<BsgExpression> by lazy {
        val expRef = Parser.newReference<BsgExpression>()
        val exp = logicalOr(expRef.lazy())
        expRef.set(exp)
        exp
    }

    val lValueExpression: Parser<BsgLValueExpression> = expression.map {
        when {
            it is BsgPostfixExpression && it.postfix is BsgPostfix.Dot -> BsgLValueExpression.Access(it.exp, it.postfix.identifier)
            it is BsgExpression.Primary && it.primary is BsgPrimary.Var -> BsgLValueExpression.Var(it.primary.identifier)
            else -> error("Invalid LValueExpression: $it")
        }
    }
}