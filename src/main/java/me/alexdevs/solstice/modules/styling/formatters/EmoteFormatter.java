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

public class EmoteFormatter {
    public static void sendEmoteMessage(ServerPlayer receiver, PlayerChatMessage message, ChatType.Bound params, ServerPlayer sender) {
        var config = Solstice.modules.getModule(StylingModule.class).getConfig();
        var playerContext = PlaceholderContext.of(sender);

        Component messageText = Components.chat(message, sender);

        var text = Format.parse(
                config.emoteFormat,
                playerContext,
                Map.of(
                        "message", messageText
                )
        );

        receiver.sendSystemMessage(text);
    }
}
