package me.alexdevs.solstice.modules.core;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerPosition;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.modules.core.commands.SolsticeCommand;
import me.alexdevs.solstice.modules.core.data.CoreConfig;
import me.alexdevs.solstice.modules.core.data.CoreLocale;
import me.alexdevs.solstice.modules.core.data.CorePlayerData;
import me.alexdevs.solstice.modules.core.data.CoreServerData;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import java.util.Date;
import java.util.UUID;

public class CoreModule {
    public static final String ID = "core";


    public CoreModule() {
        Solstice.configManager.registerData(ID, CoreConfig.class, CoreConfig::new);
        Solstice.localeManager.registerShared(CoreLocale.SHARED);

        Solstice.playerData.registerData(ID, CorePlayerData.class, CorePlayerData::new);
        Solstice.serverData.registerData(ID, CoreServerData.class, CoreServerData::new);

        CommandRegistrationCallback.EVENT.register(SolsticeCommand::new);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var player = handler.getPlayer();
            var playerData = Solstice.playerData.get(player).getData(CorePlayerData.class);
            playerData.username = player.getGameProfile().getName();
            playerData.lastSeenDate = new Date();
            playerData.ipAddress = handler.getPlayer().getIp();

            var serverData = Solstice.serverData.getData(CoreServerData.class);
            serverData.usernameCache.put(player.getUuid(), playerData.username);

            if (playerData.firstJoinedDate == null) {
                Solstice.LOGGER.info("Player {} joined for the first time!", player.getGameProfile().getName());
                playerData.firstJoinedDate = new Date();
                SolsticeEvents.WELCOME.invoker().onWelcome(player, server);
            }

            if (playerData.username != null && !playerData.username.equals(player.getGameProfile().getName())) {
                Solstice.LOGGER.info("Player {} has changed their username from {}", player.getGameProfile().getName(), playerData.username);
                SolsticeEvents.USERNAME_CHANGE.invoker().onUsernameChange(player, playerData.username);
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            var playerData = Solstice.playerData.get(handler.getPlayer()).getData(CorePlayerData.class);
            playerData.lastSeenDate = new Date();
            playerData.logoffPosition = new ServerPosition(handler.getPlayer());
            Solstice.playerData.dispose(handler.getPlayer().getUuid());
        });
    }

    public static CoreConfig getConfig() {
        return Solstice.configManager.getData(CoreConfig.class);
    }

    public static CorePlayerData getPlayerData(UUID uuid) {
        return Solstice.playerData.get(uuid).getData(CorePlayerData.class);
    }

    public static CoreServerData getServerData() {
        return Solstice.serverData.getData(CoreServerData.class);
    }

    public static String getUsername(UUID uuid) {
        return Solstice.serverData.getData(CoreServerData.class).usernameCache.getOrDefault(uuid, uuid.toString());
    }
}
