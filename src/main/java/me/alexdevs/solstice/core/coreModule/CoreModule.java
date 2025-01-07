package me.alexdevs.solstice.core.coreModule;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerPosition;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.core.coreModule.commands.ServerStatCommand;
import me.alexdevs.solstice.core.coreModule.commands.SolsticeCommand;
import me.alexdevs.solstice.core.coreModule.data.CoreConfig;
import me.alexdevs.solstice.core.coreModule.data.CoreLocale;
import me.alexdevs.solstice.core.coreModule.data.CorePlayerData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import java.util.Date;
import java.util.UUID;

public class CoreModule extends ModuleBase {
    public static final String ID = "core";

    public CoreModule() {
        super(ID);

        Solstice.configManager.registerData(ID, CoreConfig.class, CoreConfig::new);
        Solstice.localeManager.registerShared(CoreLocale.SHARED);
        Solstice.localeManager.registerModule(ID, CoreLocale.MODULE);

        Solstice.playerData.registerData(ID, CorePlayerData.class, CorePlayerData::new);

        commands.add(new SolsticeCommand(this));
        commands.add(new ServerStatCommand(this));

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

    public static String getUsername(UUID uuid) {
        var profile = Solstice.server.getUserCache().getByUuid(uuid);
        if(profile.isPresent())
            return profile.get().getName();

        return uuid.toString();
    }
}
