package me.alexdevs.solstice.modules.styling.formatters;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.styling.StylingModule;
import me.alexdevs.solstice.util.Components;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;

public class ChatFormatter {
    public static void sendChatMessage(ServerPlayerEntity receiver, SignedMessage message, MessageType.Parameters params, ServerPlayerEntity sender) {
        var text = getFormattedMessage(message, sender);

        var msgType = Solstice.server.getRegistryManager().get(RegistryKeys.MESSAGE_TYPE).getOrThrow(Solstice.CHAT_TYPE);
        var newParams = new MessageType.Parameters(msgType, text, null);

        receiver.networkHandler.sendProfilelessChatMessage(message.getContent(), newParams);
    }

    public static Text getFormattedMessage(SignedMessage message, ServerPlayerEntity player) {
        Text messageText = Components.chat(message, player);

        var locale = Solstice.localeManager.getLocale(StylingModule.ID);

        var playerContext = PlaceholderContext.of(player);
        var text = locale.get(
                "chatFormat",
                playerContext,
                Map.of(
                        "message", messageText
                )
        );
        return text;
    }
}
