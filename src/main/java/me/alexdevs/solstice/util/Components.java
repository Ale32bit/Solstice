package me.alexdevs.solstice.util;

import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.TextParserUtils;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.PatternPlaceholderParser;
import eu.pb4.placeholders.api.parsers.TextParserV1;
import me.alexdevs.solstice.core.ServiceProvider;
import me.alexdevs.solstice.util.parser.MarkdownParser;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;

public class Components {
    public static Text button(Text label, Text hoverText, String command, boolean suggest) {
        var format = suggest ? ServiceProvider.locale().commands.common.buttonSuggest : ServiceProvider.locale().commands.common.button;
        var placeholders = Map.of(
                "label", label,
                "hoverText", hoverText,
                "command", Text.of(command)
        );

        format = format.replace("{{command}}", command);
        var text = TextParserUtils.formatText(format);
        return Placeholders.parseText(text, PatternPlaceholderParser.PREDEFINED_PLACEHOLDER_PATTERN, placeholders);
    }

    public static Text button(String label, String hoverText, String command) {
        var btn = button(
                TextParserUtils.formatText(label),
                TextParserUtils.formatText(hoverText),
                command,
                false
        );

        return btn;
    }

    public static Text buttonSuggest(String label, String hoverText, String command) {
        var btn = button(
                TextParserUtils.formatText(label),
                TextParserUtils.formatText(hoverText),
                command,
                true
        );

        return btn;
    }

    public static Text chat(SignedMessage message, ServerPlayerEntity player) {
        var allowAdvancedChatFormat = Permissions.check(player, "solstice.chat.advanced");

        return chat(message.getSignedContent(), allowAdvancedChatFormat);
    }

    public static Text chat(String message, ServerPlayerEntity player) {
        var allowAdvancedChatFormat = Permissions.check(player, "solstice.chat.advanced");

        return chat(message, allowAdvancedChatFormat);
    }

    public static Text chat(String message, boolean allowAdvancedChatFormat) {
        var enableMarkdown = ServiceProvider.config().chat.enableChatMarkdown;

        for (var repl : ServiceProvider.config().chat.replacements.entrySet()) {
            message = message.replace(repl.getKey(), repl.getValue());
        }

        if (!allowAdvancedChatFormat && !enableMarkdown) {
            return Text.of(message);
        }

        NodeParser parser;
        if (allowAdvancedChatFormat) {
            parser = NodeParser.merge(TextParserV1.DEFAULT, MarkdownParser.defaultParser);
        } else {
            parser = MarkdownParser.defaultParser;
        }

        return parser.parseNode(message).toText();
    }

    public static Text chat(String message, ServerCommandSource source) {
        if (source.isExecutedByPlayer())
            return chat(message, source.getPlayer());
        return chat(message, true);
    }
}
