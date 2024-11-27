package me.alexdevs.solstice.core;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.PlayerMail;
import me.alexdevs.solstice.util.Format;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import java.util.List;
import java.util.UUID;

public class MailManager {
    public static void sendMail(UUID playerUuid, PlayerMail mail) {
        var playerState = Solstice.state.getPlayerState(playerUuid);
        playerState.mails.add(mail);
        Solstice.state.savePlayerState(playerUuid, playerState);
    }

    public static List<PlayerMail> getMailList(UUID playerUuid) {
        var playerState = Solstice.state.getPlayerState(playerUuid);
        return playerState.mails.stream().toList();
    }

    public static boolean deleteMail(UUID playerUuid, int index) {
        var playerState = Solstice.state.getPlayerState(playerUuid);
        if(index < 0 || index >= playerState.mails.size()) {
            return false;
        }
        playerState.mails.remove(index);
        Solstice.state.savePlayerState(playerUuid, playerState);
        return true;
    }

    public static void clearAllMail(UUID playerUuid) {
        var playerState = Solstice.state.getPlayerState(playerUuid);
        playerState.mails.clear();
        Solstice.state.savePlayerState(playerUuid, playerState);
    }

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var player = handler.getPlayer();
            var playerState = Solstice.state.getPlayerState(player.getUuid());
            var playerContext = PlaceholderContext.of(player);

            if(!playerState.mails.isEmpty()) {
                player.sendMessage(Format.parse(Solstice.locale().commands.mail.mailPending, playerContext));
            }
        });
    }
}
