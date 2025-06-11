package me.alexdevs.solstice.modules.styling.formatters;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.styling.StylingModule;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import me.alexdevs.solstice.api.text.Components;
import me.alexdevs.solstice.api.text.Format;
import java.util.Map;

public class ChatFormatter {
    public static void sendChatMessage(ServerPlayer receiver, PlayerChatMessage message, ChatType.Bound params, ServerPlayer sender) {
        var text = getFormattedMessage(message, sender);

        receiver.sendSystemMessage(text);
    }

    public static Component getFormattedMessage(PlayerChatMessage message, ServerPlayer player) {
        Component messageText = Components.chat(message, player);

        var config = Solstice.modules.getModule(StylingModule.class).getConfig();

        var playerContext = PlaceholderContext.of(player);
        return Format.parse(
                config.chatFormat,
                playerContext,
                Map.of(
                        "message", messageText
                )
        );
    }
}
