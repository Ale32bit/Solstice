package me.alexdevs.solstice.api.text;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.*;
import me.alexdevs.solstice.api.text.tag.PhaseGradientTag;
import me.alexdevs.solstice.api.utils.PlaceholderUtils;
import net.minecraft.network.chat.Component;
import java.util.Map;
import java.util.regex.Pattern;

public class Format {
    //? if >= 26.1
    //public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("(?<!((?<!(\\\\))\\\\))\\$[{](?<id>[^}]+)}");
    //? if < 26.1
    public static final Pattern PLACEHOLDER_PATTERN = PatternPlaceholderParser.PREDEFINED_PLACEHOLDER_PATTERN;
    public static final NodeParser LEGACY_PARSER = LegacyFormattingParser.ALL;
    public static final NodeParser PARSER;

    static {
        var gradientParser = eu.pb4.placeholders.api.parsers.TagParser.DEFAULT.copy();
        gradientParser.register(PhaseGradientTag.createTag());
        PARSER = NodeParser.builder()
                .simplifiedTextFormat()
                //? if >= 26.1
                //.serverPlaceholders()
                //? if < 26.1
                .globalPlaceholders()
                .quickText()
                .add(gradientParser)
                .build();
    }

    public static Component parse(String text) {
        return PARSER.parseNode(text)
                //? if >= 26.1
                //.toComponent();
                //? if < 26.1
                .toText();
    }

    public static Component parse(TextNode textNode, PlaceholderContext context, Map<String, Component> placeholders) {
        return new MapPlaceholderParser(PLACEHOLDER_PATTERN, placeholders).parseNode(textNode)
                //? if < 26.1
                .toText(context);
                //? if >= 26.1
                //.toComponent(context);
    }

    public static Component parse(Component text, PlaceholderContext context, Map<String, Component> placeholders) {
        return parse(TextNode.convert(text), context, placeholders);
    }

    public static Component parse(String text, PlaceholderContext context, Map<String, Component> placeholders) {
        return parse(PARSER.parseNode(text), context, placeholders);
    }

    public static Component parse(String text, PlaceholderContext context) {
        return parse(PARSER.parseNode(text), context, Map.of());
    }

    public static Component parse(String text, Map<String, Component> placeholders) {
        return new MapPlaceholderParser(PLACEHOLDER_PATTERN, placeholders).parseNode(PARSER.parseNode(text))
                //? if < 26.1
                .toText();
                //? if >= 26.1
                //.toComponent();
    }

    public static Component parse(Component text, Map<String, Component> placeholders) {
        return new MapPlaceholderParser(PLACEHOLDER_PATTERN, placeholders).parseNode(PARSER.parseNode(TextNode.convert(text)))
                //? if < 26.1
                .toText();
                //? if >= 26.1
                //.toComponent();
    }
}
