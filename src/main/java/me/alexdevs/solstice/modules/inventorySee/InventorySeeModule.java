package me.alexdevs.solstice.modules.inventorySee;

import com.mojang.authlib.GameProfile;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.inventorySee.commands.InventorySeeCommand;
import me.alexdevs.solstice.modules.inventorySee.data.InventorySeeLocale;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class InventorySeeModule extends ModuleBase {
    public static final String ID = "inventorysee";

    public InventorySeeModule() {
        super(ID);

        Solstice.localeManager.registerModule(ID, InventorySeeLocale.MODULE);

        commands.add(new InventorySeeCommand(this));
    }

    public ServerPlayerEntity loadOfflinePlayer(GameProfile profile) {
        if (isOnline(profile.getId())) {
            return null;
        }

        var playerManager = Solstice.server.getPlayerManager();
        var player = playerManager.createPlayer(profile);
        playerManager.loadPlayerData(player);
        return player;
    }

    public void saveOfflinePlayer(ServerPlayerEntity player) {
        if (isOnline(player.getUuid())) {
            Solstice.LOGGER.warn("Tried to save offline player data for a player that is online.");
            return;
        }
        var saveHandler = Solstice.server.saveHandler;
        saveHandler.savePlayerData(player);
        Solstice.server.getPlayerManager().remove(player);
    }

    public boolean isOnline(UUID uuid) {
        return Solstice.server.getPlayerManager().getPlayer(uuid) != null;
    }
}
