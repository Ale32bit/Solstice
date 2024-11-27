package me.alexdevs.solstice.core;

import me.alexdevs.solstice.api.events.PlayerTeleport;
import me.alexdevs.solstice.api.ServerPosition;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BackTracker {
    public static final ConcurrentHashMap<UUID, ServerPosition> lastPlayerPositions = new ConcurrentHashMap<>();

    public static void register() {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            lastPlayerPositions.remove(handler.getPlayer().getUuid());
        });

        PlayerTeleport.EVENT.register((player, origin, destination) -> {
            lastPlayerPositions.put(player.getUuid(), origin);
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity.isPlayer()) {
                var player = (ServerPlayerEntity) entity;
                lastPlayerPositions.put(entity.getUuid(), new ServerPosition(player));
            }
        });
    }
}
