package me.alexdevs.solstice.modules.back;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerPosition;
import me.alexdevs.solstice.api.events.PlayerTeleport;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.back.commands.BackCommand;
import me.alexdevs.solstice.modules.back.data.BackLocale;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BackModule extends ModuleBase {
    public final ConcurrentHashMap<UUID, ServerPosition> lastPlayerPositions = new ConcurrentHashMap<>();
    public static final String ID = "back";

    private final List<ModCommand<BackModule>> commands = List.of(
            new BackCommand(this)
    );

    public BackModule() {
        super(ID);
        Solstice.localeManager.registerModule(ID, BackLocale.MODULE);

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> lastPlayerPositions.remove(handler.getPlayer().getUuid()));

        PlayerTeleport.EVENT.register((player, origin, destination) -> lastPlayerPositions.put(player.getUuid(), origin));

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity.isPlayer()) {
                var player = (ServerPlayerEntity) entity;
                lastPlayerPositions.put(entity.getUuid(), new ServerPosition(player));
            }
        });
    }

    @Override
    public Collection<? extends ModCommand<?>> getCommands() {
        return List.of();
    }
}
