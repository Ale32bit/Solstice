package me.alexdevs.solstice.api.text.parser;

import eu.pb4.placeholders.api.node.DirectTextNode;
import eu.pb4.placeholders.api.node.LiteralNode;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import java.util.ArrayList;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

public class CodeParser implements NodeParser {
    public static final Pattern CODE_REGEX = Pattern.compile("(?<!\\\\)`(.*?)(?<!\\\\)`");

    @Override
    public TextNode[] parseNodes(TextNode node) {
        if (node instanceof LiteralNode literal) {
            var input = literal.value();
            var list = new ArrayList<TextNode>();
            var inputLength = input.length();

            var matcher = CODE_REGEX.matcher(input);
            int pos = 0;

            while (matcher.find()) {
                if (inputLength <= matcher.start()) {
                    break;
                }

                String betweenText = input.substring(pos, matcher.start());

                if (!betweenText.isEmpty()) {
                    list.add(new LiteralNode(betweenText));
                }

                var content = matcher.group(1);

                var display = Component.literal(content).withStyle(ChatFormatting.GRAY);
                var hover = Component.nullToEmpty("Click to copy");

                var text = Component.empty()
                        .append(display)
                        .setStyle(Style.EMPTY
                                .withHoverEvent(
                                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover)
                                )
                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, content))
                        );

                list.add(new DirectTextNode(text));

                pos = matcher.end();
            }

            if (pos < inputLength) {
                var text = input.substring(pos, inputLength);
                if (!text.isEmpty()) {
                    list.add(new LiteralNode(text));
                }
            }

            return list.toArray(TextNode[]::new);
        }
        return TextNode.array(node);
    }
}
