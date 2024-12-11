package me.alexdevs.solstice.util.parser;

import me.alexdevs.solstice.modules.core.CoreModule;
import me.alexdevs.solstice.util.Format;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.parent.ClickActionNode;
import eu.pb4.placeholders.api.node.parent.FormattingNode;
import eu.pb4.placeholders.api.node.parent.HoverNode;
import net.minecraft.text.ClickEvent;
import net.minecraft.util.Formatting;

import java.util.Map;

public class MarkdownComponentParser {
    public static TextNode spoilerFormatting(TextNode[] textNodes) {
        var text = TextNode.asSingle(textNodes);
        return new HoverNode<>(
                TextNode.array(
                        new FormattingNode(TextNode.array(TextNode.of("\u258C".repeat(text.toText().getString().length()))), Formatting.DARK_GRAY)
                ),
                HoverNode.Action.TEXT, text);
    }

    public static TextNode quoteFormatting(TextNode[] textNodes) {
        return new ClickActionNode(
                TextNode.array(
                        new HoverNode<>(
                                TextNode.array(new FormattingNode(textNodes, Formatting.GRAY)),
                                HoverNode.Action.TEXT, TextNode.of("Click to copy"))
                ),
                ClickEvent.Action.COPY_TO_CLIPBOARD, TextNode.asSingle(textNodes)
        );
    }

    public static TextNode urlFormatting(TextNode[] textNodes, TextNode url) {
        var config = CoreModule.getConfig();
        var placeholders = Map.of(
                "label", TextNode.wrap(textNodes).toText(),
                "url", url.toText()
        );
        var text = Format.parse(config.link, placeholders);
        var hover = Format.parse(config.linkHover, placeholders);

        return new HoverNode<>(TextNode.array(
                new ClickActionNode(
                        TextNode.array(
                                TextNode.convert(text)
                        ),
                        ClickEvent.Action.OPEN_URL, url)),
                HoverNode.Action.TEXT, TextNode.convert(hover)
        );
    }
}
