package me.alexdevs.solstice.api.utils;

import com.mojang.authlib.GameProfile;
import me.alexdevs.solstice.Solstice;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class PlayerUtils {
    public static boolean isOnline(UUID uuid) {
        return Solstice.server.getPlayerManager().getPlayer(uuid) != null;
    }

    public static ServerPlayerEntity loadOfflinePlayer(GameProfile profile) {
        if (isOnline(profile.getId())) {
            return null;
        }

        var playerManager = Solstice.server.getPlayerManager();
        var player = playerManager.createPlayer(profile);
        playerManager.loadPlayerData(player);
        return player;
    }

    public static void saveOfflinePlayer(ServerPlayerEntity player) {
        if (isOnline(player.getUuid())) {
            Solstice.LOGGER.warn("Tried to save offline player data for a player that is online.");
            return;
        }
        var saveHandler = Solstice.server.saveHandler;
        saveHandler.savePlayerData(player);
        Solstice.server.getPlayerManager().remove(player);
    }
}
