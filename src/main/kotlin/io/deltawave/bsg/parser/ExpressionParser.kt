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
    ) { _, exp -> BsgExpression.Parenthetical(exp) }

    fun term(exp: Parser<BsgExpression>): Parser<BsgExpression> = or(
        parenthetical(exp),
        primary
    )

    fun access(exp: Parser<BsgExpression>): Parser<BsgExpression> = sequence(
            term(exp).followedBy(Tokens.ws),
            PostfixParser.postfix(exp).many()
    ) { term, accesses -> if(accesses.isEmpty()) term else {
        accesses.fold(term) { term, postfix -> BsgPostfixExpression(term, postfix) }
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
            or(Tokens.mul, Tokens.div).source().followedBy(Tokens.ws),
            cast(exp),
        ) { _, op, exp -> Pair(op, exp) }
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
        ) { _, op, exp -> Pair(op, exp) }
            .asOptional()
    ) { mul, addExp ->
        if(addExp.isPresent)
            BsgExpression.Add(mul, addExp.get().first, addExp.get().second)
        else
            mul
    }

    fun comparison(exp: Parser<BsgExpression>): Parser<BsgExpression> = sequence(
        add(exp).followedBy(Tokens.ws),
        sequence(
            Tokens.ws,
            or(Tokens.gt, Tokens.gte, Tokens.eqeq, Tokens.lt, Tokens.lte).source().followedBy(Tokens.ws),
            add(exp)
        ) { _, op, e -> Pair(op, e) }
            .asOptional()
    ) { a, compExp -> compExp.map<BsgExpression>{ (op, e) -> BsgExpression.Comparison(a, op, e) }.orElseGet { a } }

    val expression: Parser<BsgExpression> by lazy {
        val expRef = Parser.newReference<BsgExpression>()
        val exp = comparison(expRef.lazy())
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