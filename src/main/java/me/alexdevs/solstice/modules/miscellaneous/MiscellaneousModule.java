package me.alexdevs.solstice.modules.miscellaneous;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleProperties;
import me.alexdevs.solstice.api.utils.PlayerUtils;
import me.alexdevs.solstice.modules.miscellaneous.commands.*;
import me.alexdevs.solstice.modules.miscellaneous.data.MiscellaneousLocale;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
public class MiscellaneousModule extends ModuleBase {
    public static final float DEFAULT_FLY_SPEED = 0.05F;

    private final Map<UUID, Boolean> commandSleeping = new ConcurrentHashMap<>();

    public MiscellaneousModule(ModuleProperties properties) {
        super(properties);
    }

    @Override
    public void init() {
        registerLocale(MiscellaneousLocale.MODULE);

        commands.add(new EffectsCommand(this));
        commands.add(new SleepCommand(this));
        commands.add(new NudgeCommand(this));
        commands.add(new TopCommand(this));
        commands.add(new SpeedCommand(this));
        //commands.add(new KittyCannonCommand(this));
        //commands.add(new RocketCommand(this));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> commandSleeping.remove(handler.getPlayer().getUUID()));
        EntitySleepEvents.STOP_SLEEPING.register((entity, pos) -> commandSleeping.remove(entity.getUUID()));

        EntitySleepEvents.ALLOW_RESETTING_TIME.register(player -> {
            if (commandSleeping.getOrDefault(player.getUUID(), false)) {
                //? >= 1.21.11
                //return !(!player.level().dimensionType().hasFixedTime() && player.level().getSkyDarken() < 4);
                //? < 1.21.11
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
            PlayerUtils.getLevel(player).updateSleepingPlayerList();
        }
    }
}
