package me.alexdevs.solstice.coreLegacy;

import me.alexdevs.solstice.api.PlayerMail;
import me.alexdevs.solstice.core.ServiceProvider;
import me.alexdevs.solstice.util.Format;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import java.util.List;
import java.util.UUID;

public class MailManager {
    public static void sendMail(UUID playerUuid, PlayerMail mail) {
        var playerState = ServiceProvider.state.getPlayerState(playerUuid);
        if(playerState.ignoredPlayers.contains(playerUuid)) {
            return;
        }
        playerState.mails.add(mail);
        ServiceProvider.state.savePlayerState(playerUuid, playerState);
    }

    public static List<PlayerMail> getMailList(UUID playerUuid) {
        var playerState = ServiceProvider.state.getPlayerState(playerUuid);
        return playerState.mails.stream().toList();
    }

    public static boolean deleteMail(UUID playerUuid, int index) {
        var playerState = ServiceProvider.state.getPlayerState(playerUuid);
        if(index < 0 || index >= playerState.mails.size()) {
            return false;
        }
        playerState.mails.remove(index);
        ServiceProvider.state.savePlayerState(playerUuid, playerState);
        return true;
    }

    public static void clearAllMail(UUID playerUuid) {
        var playerState = ServiceProvider.state.getPlayerState(playerUuid);
        playerState.mails.clear();
        ServiceProvider.state.savePlayerState(playerUuid, playerState);
    }

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var player = handler.getPlayer();
            var playerState = ServiceProvider.state.getPlayerState(player);
            var playerContext = PlaceholderContext.of(player);

            if(!playerState.mails.isEmpty()) {
                player.sendMessage(Format.parse(ServiceProvider.locale().commands.mail.mailPending, playerContext));
            }
        });
    }
}
