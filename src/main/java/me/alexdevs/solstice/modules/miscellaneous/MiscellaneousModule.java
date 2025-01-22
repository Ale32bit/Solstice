package me.alexdevs.solstice.modules.miscellaneous;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.miscellaneous.commands.EffectsCommand;
import me.alexdevs.solstice.modules.miscellaneous.commands.SleepCommand;
import me.alexdevs.solstice.modules.miscellaneous.data.MiscellaneousLocale;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MiscellaneousModule extends ModuleBase.Toggleable {
    public static final String ID = "miscellaneous";

    private final Map<UUID, Boolean> commandSleeping = new ConcurrentHashMap<>();

    public MiscellaneousModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.localeManager.registerModule(ID, MiscellaneousLocale.MODULE);

        commands.add(new EffectsCommand(this));
        commands.add(new SleepCommand(this));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> commandSleeping.remove(handler.getPlayer().getUuid()));
        EntitySleepEvents.STOP_SLEEPING.register((entity, pos) -> commandSleeping.remove(entity.getUuid()));
    }

    public boolean isCommandSleep(LivingEntity entity) {
        return commandSleeping.getOrDefault(entity.getUuid(), false);
    }

    /**
     * Make the entity sleep regardless of the bed check.
     * <p>
     * No, this does not euthanize the entity.
     * @param entity The entity to make sleep
     */
    public void putToSleep(LivingEntity entity) {
        commandSleeping.put(entity.getUuid(), true);
        entity.sleep(entity.getBlockPos());
        if (entity instanceof ServerPlayerEntity player) {
            player.getServerWorld().updateSleepingPlayers();
        }
    }
}
