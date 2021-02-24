package io.deltawave.bsg.parser

import io.deltawave.bsg.ast.type.BsgType
import io.deltawave.bsg.util.orNull
import org.jparsec.Parser
import org.jparsec.Parsers.or
import org.jparsec.Parsers.sequence

object TypeParser {
    val anyType: Parser<BsgType> = Tokens.any.map { BsgType.Any }

    val primitiveType: Parser<BsgType> = or(
        Tokens.char,
        Tokens.bool,
        Tokens.byte,
        Tokens.short,
        Tokens.int,
        Tokens.long,
        Tokens.ubyte,
        Tokens.ushort,
        Tokens.uint,
        Tokens.ulong,
        Tokens.void,
        Tokens.opaque
    ).source().map { BsgType.Primitive(it) }

    fun typeArgs(t: Parser<BsgType>): Parser<Map<String, BsgType>> = sequence(
        Tokens.openAngle.followedBy(Tokens.ws),
        Tokens.identifier
            .followedBy(Tokens.ws)
            .followedBy(Tokens.colon)
            .followedBy(Tokens.ws),
        t,
        sequence(
            Tokens.ws
                .followedBy(Tokens.comma)
                .followedBy(Tokens.ws),
            Tokens.identifier
                .followedBy(Tokens.ws)
                .followedBy(Tokens.colon)
                .followedBy(Tokens.ws),
            t,
        ) { _, name, type -> name to type }.many().map { it.toMap() }
            .followedBy(Tokens.ws),
        Tokens.closeAngle.followedBy(Tokens.ws)
    ) { _, name, type, nameTypes, _ -> mapOf(name to type) + nameTypes }
        .optional(emptyMap())

    fun classType(t: Parser<BsgType>): Parser<BsgType> = sequence(
            Tokens.identifier.followedBy(Tokens.ws),
            typeArgs(t)
    ) { name, typeArgs -> BsgType.Class(name, typeArgs) }

    fun methodArgs(t: Parser<BsgType>): Parser<List<BsgType>> = sequence(
            t.followedBy(Tokens.ws),
            sequence(
                    Tokens.comma.followedBy(Tokens.ws),
                    t.followedBy(Tokens.ws)
            ) { _, type -> type }.many()
    ) { type, ts -> listOf(type) + ts }
            .asOptional()
            .map { it.orNull() ?: emptyList() }

    fun methodType(t: Parser<BsgType>): Parser<BsgType> = sequence(
            Tokens.openParen.followedBy(Tokens.ws),
            methodArgs(t).followedBy(Tokens.ws),
            Tokens.closeParen
                    .followedBy(Tokens.ws)
                    .followedBy(Tokens.arrow)
                    .followedBy(Tokens.ws),
            t
    ) { _, args, _, returnType -> BsgType.Method(args, returnType) }

    fun genericType(t: Parser<BsgType>): Parser<BsgType> = sequence(
            Tokens.openAngle.followedBy(Tokens.ws),
            Tokens.identifier
                .followedBy(Tokens.ws)
                .followedBy(Tokens.colon)
                .followedBy(Tokens.ws),
            t.followedBy(Tokens.ws)
                .followedBy(Tokens.closeAngle)
    ) { _, name, rawType -> BsgType.Generic(name, rawType) }

    val type: Parser<BsgType> by lazy {
        val tRef = Parser.newReference<BsgType>()
        val t = or(
            anyType,
            primitiveType,
            classType(tRef.lazy()),
            methodType(tRef.lazy()),
            genericType(tRef.lazy())
        )
        tRef.set(t)
        t
    }
}