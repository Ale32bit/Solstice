package me.alexdevs.solstice.core.customChat;

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

public class CustomEmoteMessage {
    public static void sendEmoteMessage(ServerPlayerEntity receiver, SignedMessage message, MessageType.Parameters params) {
        var playerUuid = message.link().sender();
        var player = Solstice.server.getPlayerManager().getPlayer(playerUuid);
        var playerContext = PlaceholderContext.of(player);

        Text messageText = Components.chat(message, player);

        var text = Format.parse(
                Solstice.locale().emoteFormat,
                playerContext,
                Map.of(
                        "message", messageText
                )
        );

        var msgType = Solstice.server.getRegistryManager().get(RegistryKeys.MESSAGE_TYPE).getOrThrow(Solstice.CHAT_TYPE);
        var newParams = new MessageType.Parameters(msgType, text, null);

        receiver.networkHandler.sendChatMessage(message, newParams);
    }
}
