package me.alexdevs.solstice.util.parser;

import eu.pb4.placeholders.api.parsers.MarkdownLiteParserV1;
import eu.pb4.placeholders.api.parsers.NodeParser;

import static eu.pb4.placeholders.api.parsers.MarkdownLiteParserV1.MarkdownFormat;


public class MarkdownParser {
    public static final MarkdownFormat[] ALL = new MarkdownFormat[]{
            MarkdownFormat.QUOTE,
            MarkdownFormat.BOLD,
            MarkdownFormat.ITALIC,
            MarkdownFormat.UNDERLINE,
            MarkdownFormat.STRIKETHROUGH,
            MarkdownFormat.SPOILER,
            MarkdownFormat.URL
    };

    public static final NodeParser defaultParser = createParser(ALL);

    public static NodeParser createParser(MarkdownFormat[] capabilities) {
        var mdParser =  new MarkdownLiteParserV1(
                MarkdownComponentParser::spoilerFormatting,
                MarkdownComponentParser::quoteFormatting,
                MarkdownComponentParser::urlFormatting,
                capabilities
        );

        return NodeParser.merge(mdParser, new LinkParser());
    }
}
