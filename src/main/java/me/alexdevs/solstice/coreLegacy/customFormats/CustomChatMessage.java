package me.alexdevs.solstice.coreLegacy.customFormats;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.core.ServiceProvider;
import me.alexdevs.solstice.util.Format;
import me.alexdevs.solstice.util.Components;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;

public class CustomChatMessage {
    public static void sendChatMessage(ServerPlayerEntity receiver, SignedMessage message, MessageType.Parameters params, ServerPlayerEntity sender) {
        var text = getFormattedMessage(message, sender);

        var msgType = ServiceProvider.server.getRegistryManager().get(RegistryKeys.MESSAGE_TYPE).getOrThrow(Solstice.CHAT_TYPE);
        var newParams = new MessageType.Parameters(msgType, text, null);

        receiver.networkHandler.sendProfilelessChatMessage(message.getContent(), newParams);
    }

    public static Text getFormattedMessage(SignedMessage message, ServerPlayerEntity player) {
        Text messageText = Components.chat(message, player);

        var playerContext = PlaceholderContext.of(player);
        var text = Format.parse(
                ServiceProvider.config().formats.chatFormat,
                playerContext,
                Map.of(
                        "message", messageText
                )
        );
        return text;
    }
}
