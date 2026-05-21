package me.alexdevs.solstice.modules.back;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.api.events.PlayerTeleportCallback;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.back.commands.BackCommand;
import me.alexdevs.solstice.modules.back.data.BackConfig;
import me.alexdevs.solstice.modules.back.data.BackLocale;
import me.alexdevs.solstice.modules.back.data.BackPlayerData;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BackModule extends ModuleBase.Toggleable {
    

    public BackModule(SolsticeIdentifier id) {
        super(id);
    }

    @Override
    public void init() {
        registerLocale(BackLocale.MODULE);
        registerConfig(BackConfig.class, BackConfig::new);
        registerPlayerData(BackPlayerData.class, BackPlayerData::new);

        commands.add(new BackCommand(this));

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            var config = getConfig();

            if (!config.persistLocation) {
                clearOfflinePlayerLastLocation(handler.getPlayer().getUUID());
            } else if (config.offlineClearTimeout >= 0) {
                Solstice.scheduler.schedule(() -> clearOfflinePlayerLastLocation(handler.getPlayer().getUUID()), config.offlineClearTimeout, TimeUnit.SECONDS);
            }
        });

        PlayerTeleportCallback.EVENT.register((player, origin, destination) -> setPlayerLastLocation(player.getUUID(), origin));

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity.isAlwaysTicking()) {
                try {
                    var player = (ServerPlayer) entity;
                    setPlayerLastLocation(entity.getUUID(), new ServerLocation(player));
                } catch (ClassCastException e) {
                    // They were, in fact, not a player.
                }
            }
        });
    }

    public void clearOfflinePlayerLastLocation(UUID uuid) {
        var isOnline = Solstice.server.getPlayerList().getPlayer(uuid) != null;
        if(isOnline)
            return;

        var data = getPlayerData(uuid);
        data.lastTeleportLocation = null;
        data.lastTeleportDate = null;
    }

    public void setPlayerLastLocation(UUID uuid, ServerLocation location) {
        var data = getPlayerData(uuid);
        data.lastTeleportLocation = location;
        data.lastTeleportDate = new Date();
    }

    public @Nullable ServerLocation getPlayerLastLocation(UUID uuid) {
        var config = getConfig();
        var data = getPlayerData(uuid);

        var lastTeleportDate = data.lastTeleportDate;
        if (lastTeleportDate == null) {
            return null;
        }

        var timeDelta = getDateDifference(lastTeleportDate, new Date());
        if (config.clearTimeout >= 0 && timeDelta > config.clearTimeout) {
            return null;
        }

        return data.lastTeleportLocation;
    }

    public BackConfig getConfig() {
        return Solstice.configManager.getData(BackConfig.class);
    }

    public BackPlayerData getPlayerData(UUID uuid) {
        return Solstice.playerData.get(uuid).getData(BackPlayerData.class);
    }

    public static long getDateDifference(Date start, Date end) {
        return (end.getTime() - start.getTime()) / 1000;
    }
}
