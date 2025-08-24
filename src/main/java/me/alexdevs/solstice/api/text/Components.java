package me.alexdevs.solstice.api.text;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.text.parser.MarkdownParser;
import me.alexdevs.solstice.modules.ModuleProvider;
import me.alexdevs.solstice.modules.styling.StylingModule;
import me.alexdevs.solstice.modules.styling.data.StylingConfig;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public class Components {
    private static final Random random = new Random();

    public static Component button(Component label, Component hoverText, String command, boolean suggest) {
        var locale = Solstice.localeManager.getShared();
        var format = suggest ? locale.raw("~buttonSuggest") : locale.raw("~button");
        var placeholders = Map.of(
                "label", label,
                "hoverText", hoverText,
                "command", Component.nullToEmpty(command)
        );

        var text = Format.parse(format);
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
        var allowLegacyChatFormat = Permissions.check(player, StylingModule.LEGACY_CHAT_FORMATTING_PERMISSION);

        var set = EnumSet.noneOf(TextCapabilities.class);
        if (allowAdvancedChatFormat) {
            set.add(TextCapabilities.ADVANCED);
        }

        if (allowLegacyChatFormat) {
            set.add(TextCapabilities.LEGACY);
        }

        if (ModuleProvider.STYLING.isEnabled()) {
            var config = Solstice.configManager.getData(StylingConfig.class);
            if (config.enableMarkdown) {
                set.add(TextCapabilities.MARKDOWN);
            }
        }

        return chat(message.signedContent(), set, player.createCommandSourceStack());
    }

    public static Component chat(String message, ServerPlayer player) {
        var allowAdvancedChatFormat = Permissions.check(player, StylingModule.ADVANCED_CHAT_FORMATTING_PERMISSION);
        var allowLegacyChatFormat = Permissions.check(player, StylingModule.LEGACY_CHAT_FORMATTING_PERMISSION);

        var set = EnumSet.noneOf(TextCapabilities.class);
        if (allowAdvancedChatFormat) {
            set.add(TextCapabilities.ADVANCED);
        }

        if (allowLegacyChatFormat) {
            set.add(TextCapabilities.LEGACY);
        }

        if (ModuleProvider.STYLING.isEnabled()) {
            var config = Solstice.configManager.getData(StylingConfig.class);
            if (config.enableMarkdown) {
                set.add(TextCapabilities.MARKDOWN);
            }
        }

        return chat(message, set, player.createCommandSourceStack());
    }

    public static Component chat(String message, EnumSet<TextCapabilities> capabilities, CommandSourceStack source) {
        var enableMarkdown = capabilities.contains(TextCapabilities.MARKDOWN);
        var enableAdvanced = capabilities.contains(TextCapabilities.ADVANCED);
        var enableLegacy = capabilities.contains(TextCapabilities.LEGACY);

        var placeholders = new HashMap<String, Component>();
        var context = PlaceholderContext.of(source);
        if (ModuleProvider.STYLING.isEnabled()) {
            var config = Solstice.configManager.getData(StylingConfig.class);
            for (var repl : config.replacements.entrySet()) {
                if (message.contains(repl.getKey())) {
                    var key = Integer.toHexString(random.nextInt(16 ^ 8));
                    message = message.replace(repl.getKey(), String.format("${%s}", key));
                    var value = repl.getValue().replace("\\", "\\\\");
                    placeholders.put(key, Format.parse(value, context));
                }
            }
        }


        if (capabilities.isEmpty()) {
            return Placeholders.parseText(TextNode.of(message), Format.PLACEHOLDER_PATTERN, placeholders);
        }

        var parsers = new ArrayList<NodeParser>();
        if (enableAdvanced) {
            parsers.add(Format.PARSER);
        }

        if (enableLegacy) {
            parsers.add(Format.LEGACY_PARSER);
        }

        if (enableMarkdown) {
            parsers.add(MarkdownParser.defaultParser);
        }

        var parser = NodeParser.merge(parsers.toArray(NodeParser[]::new));

        var node = parser.parseNode(message);
        return Placeholders.parseText(node, Format.PLACEHOLDER_PATTERN, placeholders);
    }

    public static Component chat(String message, CommandSourceStack source) {
        if (source.isPlayer())
            return chat(message, source.getPlayer());

        return chat(message, EnumSet.allOf(TextCapabilities.class), source);
    }
}
