package me.alexdevs.solstice.api.text;

import eu.pb4.placeholders.api.node.LiteralNode;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.TranslatedNode;
import eu.pb4.placeholders.api.node.parent.ParentTextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record MapPlaceholderParser(Pattern pattern, Map<String, Component> placeholders) implements NodeParser {
    @Override
    public TextNode[] parseNodes(TextNode text) {
        if (text instanceof TranslatedNode translatedNode) {
            return new TextNode[]{translatedNode.transform(this)};
        } else if (text instanceof LiteralNode literalNode) {
            var out = new ArrayList<TextNode>();

            String string = literalNode.value();
            Matcher matcher = pattern.matcher(string);
            int previousEnd = 0;

            while (matcher.find()) {
                var key = matcher.group("id");
                int start = matcher.start();
                int end = matcher.end();

                var replacement = placeholders.get(key);

                if (replacement != null) {
                    if (start != 0) {
                        out.add(new LiteralNode(string.substring(previousEnd, start)));
                    }
                    out.add(TextNode.convert(replacement));
                    previousEnd = end;
                } else {
                    matcher.region(start + 1, string.length());
                }
            }

            if (previousEnd != string.length()) {
                out.add(new LiteralNode(string.substring(previousEnd)));
            }

            return out.toArray(new TextNode[0]);
        } else if (text instanceof ParentTextNode parentNode) {
            var out = new ArrayList<TextNode>();
            for (var child : parentNode.getChildren()) {
                out.add(TextNode.asSingle(this.parseNodes(child)));
            }
            return new TextNode[]{parentNode.copyWith(out.toArray(new TextNode[0]), this)};
        }

        return new TextNode[]{text};
    }
}
