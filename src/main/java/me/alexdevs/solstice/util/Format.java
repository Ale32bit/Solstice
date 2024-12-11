package me.alexdevs.solstice.util;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.TextParserUtils;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.PatternPlaceholderParser;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.regex.Pattern;

public class Format {
    public static final Pattern PLACEHOLDER_PATTERN = PatternPlaceholderParser.PREDEFINED_PLACEHOLDER_PATTERN;

    public static Text parse(String text) {
        return TextParserUtils.formatText(text);
    }

    public static Text parse(TextNode textNode, PlaceholderContext context, Map<String, Text> placeholders) {
        var predefinedNode = Placeholders.parseNodes(textNode, PLACEHOLDER_PATTERN, placeholders);
        return Placeholders.parseText(predefinedNode, context);
    }

    public static Text parse(Text text, PlaceholderContext context, Map<String, Text> placeholders) {
        return parse(TextNode.convert(text), context, placeholders);
    }

    public static Text parse(String text, PlaceholderContext context, Map<String, Text> placeholders) {
        return parse(parse(text), context, placeholders);
    }

    public static Text parse(String text, PlaceholderContext context) {
        return parse(parse(text), context, Map.of());
    }

    public static Text parse(String text, Map<String, Text> placeholders) {
        return Placeholders.parseText(parse(text), PLACEHOLDER_PATTERN, placeholders);
    }

    public static Text parse(Text text, Map<String, Text> placeholders) {
        return Placeholders.parseText(TextNode.convert(text), PLACEHOLDER_PATTERN, placeholders);
    }
}
