package me.alexdevs.solstice.modules.back;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.api.events.PlayerTeleportCallback;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.back.commands.BackCommand;
import me.alexdevs.solstice.modules.back.data.BackLocale;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
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

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> lastPlayerPositions.remove(handler.getPlayer().getUUID()));

        PlayerTeleportCallback.EVENT.register((player, origin, destination) -> lastPlayerPositions.put(player.getUUID(), origin));

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
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
