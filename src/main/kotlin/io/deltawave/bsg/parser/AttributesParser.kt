package io.deltawave.bsg.parser

import org.jparsec.Parser
import org.jparsec.Parsers.sequence

object AttributesParser {
    val attributes: Parser<Set<String>> = sequence(
            Tokens.openSquare.followedBy(Tokens.ws),
            sequence(
                    Tokens.identifier.followedBy(Tokens.ws),
                    sequence(
                            Tokens.comma.followedBy(Tokens.ws),
                            Tokens.identifier.followedBy(Tokens.ws)
                    ) { _, attr -> attr }.many()
            ) { attr, attrs -> listOf(attr) + attrs }.optional(emptyList()),
            Tokens.closeSquare,
    ) { _, attrs, _ -> attrs.toSet() }
}