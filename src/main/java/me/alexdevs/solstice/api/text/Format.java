package me.alexdevs.solstice.api.text;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.*;
import me.alexdevs.solstice.api.text.tag.PhaseGradientTag;
import net.minecraft.network.chat.Component;
import java.util.Map;
import java.util.regex.Pattern;

public class Format {
    public static final Pattern PLACEHOLDER_PATTERN = PatternPlaceholderParser.PREDEFINED_PLACEHOLDER_PATTERN;
    public static final NodeParser LEGACY_PARSER = LegacyFormattingParser.ALL;
    public static final NodeParser PARSER;

    static {
        var parser = TextParserV1.createDefault();
        parser.register(PhaseGradientTag.createTag());
        PARSER = parser;
    }

    public static Component parse(String text) {
        return PARSER.parseNode(text).toText();
    }

    public static Component parse(TextNode textNode, PlaceholderContext context, Map<String, Component> placeholders) {
        var predefinedNode = Placeholders.parseNodes(textNode, PLACEHOLDER_PATTERN, placeholders);
        return Placeholders.parseText(predefinedNode, context);
    }

    public static Component parse(Component text, PlaceholderContext context, Map<String, Component> placeholders) {
        return parse(TextNode.convert(text), context, placeholders);
    }

    public static Component parse(String text, PlaceholderContext context, Map<String, Component> placeholders) {
        return parse(parse(text), context, placeholders);
    }

    public static Component parse(String text, PlaceholderContext context) {
        return parse(parse(text), context, Map.of());
    }

    public static Component parse(String text, Map<String, Component> placeholders) {
        return Placeholders.parseText(parse(text), PLACEHOLDER_PATTERN, placeholders);
    }

    public static Component parse(Component text, Map<String, Component> placeholders) {
        return Placeholders.parseText(TextNode.convert(text), PLACEHOLDER_PATTERN, placeholders);
    }
}
