package me.alexdevs.solstice.core.customFormats;

import me.alexdevs.solstice.Solstice;
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
    public static void sendChatMessage(ServerPlayerEntity receiver, SignedMessage message, MessageType.Parameters params) {
        var playerUuid = message.link().sender();
        var player = Solstice.server.getPlayerManager().getPlayer(playerUuid);

        var text = getFormattedMessage(message, player);

        var msgType = Solstice.server.getRegistryManager().get(RegistryKeys.MESSAGE_TYPE).getOrThrow(Solstice.CHAT_TYPE);
        var newParams = new MessageType.Parameters(msgType, text, null);

        receiver.networkHandler.sendChatMessage(message, newParams);
    }

    public static Text getFormattedMessage(SignedMessage message, ServerPlayerEntity player) {
        Text messageText = Components.chat(message, player);

        var playerContext = PlaceholderContext.of(player);
        var text = Format.parse(
                Solstice.config().formats.chatFormat,
                playerContext,
                Map.of(
                        "message", messageText
                )
        );
        return text;
    }
}
