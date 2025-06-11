package me.alexdevs.solstice.api.text;

import eu.pb4.placeholders.api.TextParserUtils;
import eu.pb4.placeholders.api.parsers.NodeParser;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.permissions.Permissions;
import me.alexdevs.solstice.api.text.parser.MarkdownParser;
import me.alexdevs.solstice.core.coreModule.CoreModule;
import me.alexdevs.solstice.modules.styling.StylingModule;
import me.alexdevs.solstice.modules.styling.data.StylingConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import java.util.Map;

public class Components {
    public static Component button(Component label, Component hoverText, String command, boolean suggest) {
        var locale = Solstice.localeManager.getLocale(CoreModule.ID);
        var format = suggest ? locale.raw("~buttonSuggest") : locale.raw("~button");
        var placeholders = Map.of(
                "label", label,
                "hoverText", hoverText,
                "command", Component.nullToEmpty(command)
        );

        var text = TextParserUtils.formatText(format);
        return Format.parse(text, placeholders);
    }

    public static Component button(String label, String hoverText, String command) {

        return button(
                Format.parse(label),
                Format.parse(hoverText),
                command,
                false
        );
    }

    public static Component buttonSuggest(String label, String hoverText, String command) {

        return button(
                Format.parse(label),
                Format.parse(hoverText),
                command,
                true
        );
    }

    public static Component chat(PlayerChatMessage message, ServerPlayer player) {
        var allowAdvancedChatFormat = Permissions.check(player, StylingModule.ADVANCED_CHAT_FORMATTING_PERMISSION);

        return chat(message.signedContent(), allowAdvancedChatFormat);
    }

    public static Component chat(String message, ServerPlayer player) {
        var allowAdvancedChatFormat = Permissions.check(player, StylingModule.ADVANCED_CHAT_FORMATTING_PERMISSION);

        return chat(message, allowAdvancedChatFormat);
    }

    public static Component chat(String message, boolean allowAdvancedChatFormat) {
        var config = Solstice.configManager.getData(StylingConfig.class);
        var enableMarkdown = config.enableMarkdown;

        for (var repl : config.replacements.entrySet()) {
            message = message.replace(repl.getKey(), repl.getValue());
        }

        if (!allowAdvancedChatFormat && !enableMarkdown) {
            return Component.nullToEmpty(message);
        }

        NodeParser parser;
        if (allowAdvancedChatFormat) {
            parser = NodeParser.merge(Format.PARSER, MarkdownParser.defaultParser);
        } else {
            parser = MarkdownParser.defaultParser;
        }

        return parser.parseNode(message).toText();
    }

    public static Component chat(String message, CommandSourceStack source) {
        if (source.isPlayer())
            return chat(message, source.getPlayer());
        return chat(message, true);
    }
}
