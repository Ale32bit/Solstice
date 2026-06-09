package me.alexdevs.solstice.integrations;

import me.drex.vanish.api.VanishAPI;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class VanishIntegration {
    public static boolean isAvailable() {
        return FabricLoader.getInstance().isModLoaded("melius-vanish");
    }

    public static List<ServerPlayer> getVisiblePlayers(CommandSourceStack source) {
        return VanishAPI.getVisiblePlayers(source);
    }

    public static String[] getVisiblePlayersAsString(CommandSourceStack source) {
        return getVisiblePlayers(source).stream()
                .map(player -> player.getName().getString())
                .toList().toArray(String[]::new);
    }
}
