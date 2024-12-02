package me.alexdevs.solstice.coreLegacy;

import me.alexdevs.solstice.core.ServiceProvider;
import me.alexdevs.solstice.util.Format;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;


public class MuteManager {
    public static void register() {
        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((signedMessage, player, parameters) -> {
            var playerState = ServiceProvider.state.getPlayerState(player);
            if (playerState.muted) {
                player.sendMessage(Format.parse(ServiceProvider.locale().youAreMuted));
                return false;
            }
            return true;
        });
    }
}
