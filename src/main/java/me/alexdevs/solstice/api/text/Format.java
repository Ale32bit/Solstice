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
    //? if >= 26.1 {
    /*public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("(?<!((?<!(\\\\))\\\\))\\$[{](?<id>[^}]+)}");
    *///? } elif >= 1.21.1 {
    public static final Pattern PLACEHOLDER_PATTERN = PatternPlaceholderParser.PREDEFINED_PLACEHOLDER_PATTERN;
    //? }
    public static final NodeParser LEGACY_PARSER = LegacyFormattingParser.ALL;
    public static final NodeParser PARSER;

    static {
        //? if >= 26.1 {
        /*var parser = eu.pb4.placeholders.api.parsers.TagParser.DEFAULT.copy();

        *///? } elif >= 1.21.1 {
        var parser = TextParserV1.createDefault();
        //? }
        parser.register(PhaseGradientTag.createTag());
        PARSER = parser;
    }

    public static Component parse(String text) {
        //? if >= 26.1 {
        /*return PARSER.parseNode(text).toComponent();
        *///? } elif >= 1.21.1 {
        return PARSER.parseNode(text).toText();
        //? }
    }

    public static Component parse(TextNode textNode, PlaceholderContext context, Map<String, Component> placeholders) {
        //? if >= 26.1 {
        /*var predefinedNode = new MapPlaceholderParser(PLACEHOLDER_PATTERN, placeholders).parseNode(textNode);
        return Placeholders.COMMON_PLACEHOLDER_PARSER.parseNode(predefinedNode)
                .toComponent(ParserContext.of(PlaceholderContext.COMMON_KEY, context));
        *///? } elif >= 1.21.1 {
        var predefinedNode = Placeholders.parseNodes(textNode, PLACEHOLDER_PATTERN, placeholders);
        return Placeholders.parseText(predefinedNode, context);
        //? }
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
        //? if >= 26.1 {
        /*return new MapPlaceholderParser(PLACEHOLDER_PATTERN, placeholders).parseNode(TextNode.convert(parse(text))).toComponent();
        *///? } elif >= 1.21.1 {
        return Placeholders.parseText(parse(text), PLACEHOLDER_PATTERN, placeholders);
        //? }
    }

    public static Component parse(Component text, Map<String, Component> placeholders) {
        //? if >= 26.1 {
        /*return new MapPlaceholderParser(PLACEHOLDER_PATTERN, placeholders).parseNode(TextNode.convert(text)).toComponent();
        *///? } elif >= 1.21.1 {
        return Placeholders.parseText(TextNode.convert(text), PLACEHOLDER_PATTERN, placeholders);
        //? }
    }
}
