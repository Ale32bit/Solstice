package me.alexdevs.solstice.api.utils;

import com.mojang.authlib.GameProfile;
import me.alexdevs.solstice.Solstice;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import java.util.UUID;

public class PlayerUtils {
    public static boolean isOnline(UUID uuid) {
        return Solstice.server.getPlayerList().getPlayer(uuid) != null;
    }

    public static ServerPlayer loadOfflinePlayer(GameProfile profile) {
        if (isOnline(profile.getId())) {
            return null;
        }

        var playerManager = Solstice.server.getPlayerList();
        var player = playerManager.getPlayerForLogin(profile, ClientInformation.createDefault());
        playerManager.load(player);
        return player;
    }

    public static void saveOfflinePlayer(ServerPlayer player) {
        if (isOnline(player.getUUID())) {
            Solstice.LOGGER.warn("Tried to save offline player data for a player that is online.");
            return;
        }
        var saveHandler = Solstice.server.playerDataStorage;
        saveHandler.save(player);
        Solstice.server.getPlayerList().remove(player);
    }
}
