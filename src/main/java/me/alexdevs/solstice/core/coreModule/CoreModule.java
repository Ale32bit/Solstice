package me.alexdevs.solstice.core.coreModule;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.events.WorldSaveCallback;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.core.coreModule.commands.PingCommand;
import me.alexdevs.solstice.core.coreModule.commands.ServerStatCommand;
import me.alexdevs.solstice.core.coreModule.commands.SolsticeCommand;
import me.alexdevs.solstice.core.coreModule.data.CoreConfig;
import me.alexdevs.solstice.core.coreModule.data.CoreLocale;
import me.alexdevs.solstice.core.coreModule.data.CorePlayerData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.Entity;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CoreModule extends ModuleBase {
    public static final String ID = "core";

    public CoreModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.configManager.registerData(ID, CoreConfig.class, CoreConfig::new);
        Solstice.localeManager.registerShared(CoreLocale.SHARED);
        Solstice.localeManager.registerModule(ID, CoreLocale.MODULE);

        Solstice.playerData.registerData(ID, CorePlayerData.class, CorePlayerData::new);

        commands.add(new SolsticeCommand(this));
        commands.add(new ServerStatCommand(this));
        commands.add(new PingCommand(this));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            Solstice.getUserCache().add(handler.getPlayer().getGameProfile());
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
            playerData.logoffPosition = new ServerLocation(handler.getPlayer());
            Solstice.scheduler.schedule(() -> {
                Solstice.playerData.dispose(handler.getPlayer().getUuid());
            }, 1, TimeUnit.SECONDS);
        });

        WorldSaveCallback.EVENT.register((server, suppressLogs, flush, force) -> {
            var uuids = server.getPlayerManager().getPlayerList().stream().map(Entity::getUuid).toList();
            Solstice.playerData.disposeMissing(uuids);
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
