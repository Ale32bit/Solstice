package me.alexdevs.solstice.modules.core;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.locale.Locale;
import me.alexdevs.solstice.modules.core.commands.SolsticeCommand;
import me.alexdevs.solstice.modules.core.data.CoreLocale;
import me.alexdevs.solstice.modules.core.data.CorePlayerData;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import java.util.Date;

public class CoreModule {
    public static final String ID = "core";
    private final Locale locale = Solstice.newLocaleManager.getLocale(ID);

    public CoreModule() {
        Solstice.newLocaleManager.registerShared(CoreLocale.SHARED);

        Solstice.playerData.registerData(ID, CorePlayerData.class, CorePlayerData::new);

        CommandRegistrationCallback.EVENT.register((dispatcher, registry, environment) -> {
            new SolsticeCommand(dispatcher, registry, environment);
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var player = handler.getPlayer();
            var playerData = Solstice.playerData.get(player).getData(CorePlayerData.class);
            playerData.username = player.getGameProfile().getName();
            playerData.lastSeenDate = new Date();
            playerData.ipAddress = handler.getPlayer().getIp();

            if (playerData.firstJoinedDate == null) {
                Solstice.LOGGER.info("Player {} joined for the first time!", player.getGameProfile().getName());
                playerData.firstJoinedDate = new Date();
                SolsticeEvents.WELCOME.invoker().onWelcome(player, server);
//                var spawnPosition = serverState.spawn;
//
//                if (spawnPosition != null) {
//                    spawnPosition.teleport(player, false);
//                }
            }

            if (playerData.username != null && !playerData.username.equals(player.getGameProfile().getName())) {
                Solstice.LOGGER.info("Player {} has changed their username from {}", player.getGameProfile().getName(), playerData.username);
                SolsticeEvents.USERNAME_CHANGE.invoker().onUsernameChange(player, playerData.username);
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            var playerData = Solstice.playerData.get(handler.getPlayer()).getData(CorePlayerData.class);
            playerData.lastSeenDate = new Date();
            Solstice.playerData.dispose(handler.getPlayer().getUuid());
        });
    }
}
