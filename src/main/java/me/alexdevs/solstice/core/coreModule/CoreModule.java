package me.alexdevs.solstice.core.coreModule;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.events.WorldSaveCallback;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleProperties;
import me.alexdevs.solstice.api.utils.PlayerUtils;
import me.alexdevs.solstice.core.coreModule.commands.PingCommand;
import me.alexdevs.solstice.core.coreModule.commands.ServerStatCommand;
import me.alexdevs.solstice.core.coreModule.commands.SolsticeCommand;
import me.alexdevs.solstice.core.coreModule.data.CoreConfig;
import me.alexdevs.solstice.core.coreModule.data.CoreLocale;
import me.alexdevs.solstice.core.coreModule.data.CorePlayerData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
import com.mojang.authlib.GameProfile;
import net.minecraft.world.entity.Entity;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CoreModule extends ModuleBase {
    public CoreModule(ModuleProperties properties) {
        super(properties);
    }

    @Override
    public void init() {
        registerConfig(CoreConfig.class, CoreConfig::new);
        registerSharedLocale(CoreLocale.SHARED);
        registerLocale(CoreLocale.MODULE);
        registerPlayerData(CorePlayerData.class, CorePlayerData::new);

        commands.add(new SolsticeCommand(this));
        commands.add(new ServerStatCommand(this));
        commands.add(new PingCommand(this));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            //? < 1.21.11
            Solstice.getUserCache().add(handler.getPlayer().getGameProfile());
            //? >= 1.21.11
            //Solstice.getUserCache().add(handler.getPlayer().getGameProfile());

            var player = handler.getPlayer();
            var playerData = Solstice.playerData.get(player).getData(CorePlayerData.class);
            var playerName = PlayerUtils.getName(player.getGameProfile());
            playerData.username = playerName;
            playerData.lastSeenDate = new Date();
            playerData.ipAddress = handler.getPlayer().getIpAddress();

            if (playerData.firstJoinedDate == null) {
                Solstice.LOGGER.info("Player {} joined for the first time!", playerName);
                playerData.firstJoinedDate = new Date();
                SolsticeEvents.WELCOME.invoker().onWelcome(player, server);
            }

            if (playerData.username != null && !playerData.username.equals(playerName)) {
                Solstice.LOGGER.info("Player {} has changed their username from {}", playerName, playerData.username);
                SolsticeEvents.USERNAME_CHANGE.invoker().onUsernameChange(player, playerData.username);
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            var playerData = Solstice.playerData.get(handler.getPlayer()).getData(CorePlayerData.class);
            playerData.lastSeenDate = new Date();
            playerData.logoffPosition = new ServerLocation(handler.getPlayer());
            Solstice.scheduler.schedule(() -> {
                Solstice.playerData.dispose(handler.getPlayer().getUUID());
            }, 1, TimeUnit.SECONDS);
        });

        WorldSaveCallback.EVENT.register((server, suppressLogs, flush, force) -> {
            var uuids = server.getPlayerList().getPlayers().stream().map(Entity::getUUID).toList();
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
        //? >= 1.21.11
        //var profile = Solstice.server.services().profileResolver().fetchById(uuid);
        //? < 1.21.11
        var profile = Solstice.server.getProfileCache().get(uuid);


        if (profile.isPresent()) {
            return PlayerUtils.getName(profile.get());
        }

        return uuid.toString();
    }
}
