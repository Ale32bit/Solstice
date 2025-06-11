package me.alexdevs.solstice.modules.back;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.api.events.PlayerTeleportCallback;
import me.alexdevs.solstice.api.events.proxy.ProxyServerLivingEntityEvents;
import me.alexdevs.solstice.api.events.proxy.ProxyServerPlayConnectionEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.back.commands.BackCommand;
import me.alexdevs.solstice.modules.back.data.BackLocale;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BackModule extends ModuleBase.Toggleable {
    public static final String ID = "back";
    public final ConcurrentHashMap<UUID, ServerLocation> lastPlayerPositions = new ConcurrentHashMap<>();

    public BackModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.localeManager.registerModule(ID, BackLocale.MODULE);

        commands.add(new BackCommand(this));

        ProxyServerPlayConnectionEvents.DISCONNECT.register((player, server) -> lastPlayerPositions.remove(player.getUUID()));

        PlayerTeleportCallback.EVENT.register((player, origin, destination) -> lastPlayerPositions.put(
                player.getUUID(),
                origin
        ));

        ProxyServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity.isAlwaysTicking()) {
                try {
                    var player = (ServerPlayer) entity;

                    lastPlayerPositions.put(entity.getUUID(), new ServerLocation(player));
                } catch (ClassCastException e) {
                    // They were, in fact, not a player.
                }
            }
        });
    }
}
