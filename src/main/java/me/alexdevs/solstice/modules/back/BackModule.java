package me.alexdevs.solstice.modules.back;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerPosition;
import me.alexdevs.solstice.api.events.PlayerTeleport;
import me.alexdevs.solstice.modules.back.commands.BackCommand;
import me.alexdevs.solstice.modules.back.data.BackLocale;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BackModule {
    public static final ConcurrentHashMap<UUID, ServerPosition> lastPlayerPositions = new ConcurrentHashMap<>();
    public static final String ID = "back";

    public BackModule() {
        Solstice.newLocaleManager.registerModule(ID, BackLocale.MODULE);

        CommandRegistrationCallback.EVENT.register(BackCommand::new);

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
