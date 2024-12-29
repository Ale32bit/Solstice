package me.alexdevs.solstice.modules.styling.formatters;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.styling.StylingModule;
import me.alexdevs.solstice.api.text.Components;
import me.alexdevs.solstice.api.text.Format;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;

public class ChatFormatter {
    public static void sendChatMessage(ServerPlayerEntity receiver, SignedMessage message, MessageType.Parameters params, ServerPlayerEntity sender) {
        var text = getFormattedMessage(message, sender);

        receiver.sendMessage(text);

//        var msgType = Solstice.server.getRegistryManager().get(RegistryKeys.MESSAGE_TYPE).getOrThrow(Solstice.CHAT_TYPE);
//        var newParams = new MessageType.Parameters(msgType, text, null);
//
//        receiver.networkHandler.sendProfilelessChatMessage(message.getContent(), newParams);
    }

    public static Text getFormattedMessage(SignedMessage message, ServerPlayerEntity player) {
        Text messageText = Components.chat(message, player);

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
