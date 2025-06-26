package me.alexdevs.solstice.modules.miscellaneous;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.miscellaneous.commands.EffectsCommand;
import me.alexdevs.solstice.modules.miscellaneous.commands.NudgeCommand;
import me.alexdevs.solstice.modules.miscellaneous.commands.SleepCommand;
import me.alexdevs.solstice.modules.miscellaneous.commands.TopCommand;
import me.alexdevs.solstice.modules.miscellaneous.data.MiscellaneousLocale;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
public class MiscellaneousModule extends ModuleBase.Toggleable {
    

    private final Map<UUID, Boolean> commandSleeping = new ConcurrentHashMap<>();

    public MiscellaneousModule(ResourceLocation id) {
        super(id);
    }

    @Override
    public void init() {
        registerLocale(MiscellaneousLocale.MODULE);

        commands.add(new EffectsCommand(this));
        commands.add(new SleepCommand(this));
        commands.add(new NudgeCommand(this));
        commands.add(new TopCommand(this));
        //commands.add(new KittyCannonCommand(this));
        //commands.add(new RocketCommand(this));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> commandSleeping.remove(handler.getPlayer().getUUID()));
        EntitySleepEvents.STOP_SLEEPING.register((entity, pos) -> commandSleeping.remove(entity.getUUID()));

        EntitySleepEvents.ALLOW_SLEEP_TIME.register((player, pos, vanillaResult) -> {
            if (commandSleeping.getOrDefault(player.getUUID(), false)) {
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        });

        EntitySleepEvents.ALLOW_RESETTING_TIME.register(player -> {
            if (commandSleeping.getOrDefault(player.getUUID(), false)) {
                return !player.level().isDay();
            }

            return true;
        });
    }

    public boolean isCommandSleep(LivingEntity entity) {
        return commandSleeping.getOrDefault(entity.getUUID(), false);
    }

    /**
     * Make the entity sleep regardless of the bed check.
     * <p>
     * No, this does not euthanize the entity.
     *
     * @param entity The entity to make sleep
     */
    public void putToSleep(LivingEntity entity) {
        commandSleeping.put(entity.getUUID(), true);
        entity.startSleeping(entity.blockPosition());
        if (entity instanceof ServerPlayer player) {
            player.serverLevel().updateSleepingPlayerList();
        }
    }
}
