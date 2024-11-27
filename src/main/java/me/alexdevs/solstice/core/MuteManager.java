package me.alexdevs.solstice.core;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.util.Format;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;


public class MuteManager {
    public static void register() {
        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((signedMessage, player, parameters) -> {
            var playerState = Solstice.state.getPlayerState(player.getUuid());
            if (playerState.muted) {
                player.sendMessage(Format.parse(Solstice.locale().youAreMuted));
                return false;
            }
            return true;
        });
    }
}
