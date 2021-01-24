package io.deltawave.bsg.parser

import io.deltawave.bsg.ast.ReservedWords.Let
import io.deltawave.bsg.ast.ReservedWords.Var
import org.jparsec.Parser
import org.jparsec.Scanners
import org.jparsec.pattern.CharPredicates
import org.jparsec.pattern.Patterns

object Tokens {
    val ws: Parser<Void> = Patterns.many(CharPredicates.IS_WHITESPACE).toScanner("whitespace")

    val classKeyword: Parser<Void> = Scanners.string("class")
    val extendsKeyword: Parser<Void> = Scanners.string("extends")
    val implementsKeyword: Parser<Void> = Scanners.string("implements")
    val returnKeyword: Parser<Void> = Scanners.string("return")
    val newKeyword: Parser<Void> = Scanners.string("new")

    val varKeyword: Parser<Var> = Scanners.string("var").map { Var }

    val char: Parser<Void> = Scanners.string("Char")
    val bool: Parser<Void> = Scanners.string("Bool")
    val byte: Parser<Void> = Scanners.string("Byte")
    val short: Parser<Void> = Scanners.string("Short")
    val int: Parser<Void> = Scanners.string("Int")
    val long: Parser<Void> = Scanners.string("Void")
    val ubyte: Parser<Void> = Scanners.string("UByte")
    val ushort: Parser<Void> = Scanners.string("UShort")
    val uint: Parser<Void> = Scanners.string("UInt")
    val ulong: Parser<Void> = Scanners.string("ULong")

    val openCurly: Parser<Void> = Scanners.string("{")
    val closeCurly: Parser<Void> = Scanners.string("}")
    val openParen: Parser<Void> = Scanners.string("(")
    val closeParen: Parser<Void> = Scanners.string(")")
    val openSquare: Parser<Void> = Scanners.string("[")
    val closeSquare: Parser<Void> = Scanners.string("]")

    val colon: Parser<Void> = Scanners.string(":")
    val semicolon: Parser<Void> = Scanners.string(";")
    val dot: Parser<Void> = Scanners.string(".")
    val comma: Parser<Void> = Scanners.string(",")
    val equals: Parser<Void> = Scanners.string("=")
    val not: Parser<Void> = Scanners.string("!")
    val arrow: Parser<Void> = Scanners.string("->")

    val plus: Parser<Void> = Scanners.string("+")
    val minus: Parser<Void> = Scanners.string("-")
    val mul: Parser<Void> = Scanners.string("*")
    val div: Parser<Void> = Scanners.string("/")

    val identifier: Parser<String> = Scanners.IDENTIFIER
    val integerLiteral: Parser<String> = Scanners.INTEGER
    val floatLiteral: Parser<String> = Scanners.INTEGER.followedBy(dot).followedBy(Scanners.INTEGER)
}