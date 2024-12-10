package me.alexdevs.solstice.modules.styling.formatters;

import me.alexdevs.solstice.util.Format;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.util.Components;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;

public class EmoteFormatter {
    public static void sendEmoteMessage(ServerPlayerEntity receiver, SignedMessage message, MessageType.Parameters params, ServerPlayerEntity sender) {
        var playerContext = PlaceholderContext.of(sender);

        Text messageText = Components.chat(message, sender);

        var text = Format.parse(
                Solstice.config().formats.emoteFormat,
                playerContext,
                Map.of(
                        "message", messageText
                )
        );

        var msgType = Solstice.server.getRegistryManager().get(RegistryKeys.MESSAGE_TYPE).getOrThrow(Solstice.CHAT_TYPE);
        var newParams = new MessageType.Parameters(msgType, text, null);

        receiver.networkHandler.sendProfilelessChatMessage(message.getContent(), newParams);
    }
}
