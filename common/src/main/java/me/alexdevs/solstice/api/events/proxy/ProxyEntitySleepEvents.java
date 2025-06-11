package me.alexdevs.solstice.api.events.proxy;

import me.alexdevs.solstice.api.events.backend.Event;
import me.alexdevs.solstice.api.events.backend.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ProxyEntitySleepEvents {
    /**
     * An event that is called when an entity stops sleeping and wakes up.
     */
    public static final Event<StopSleeping> STOP_SLEEPING = EventFactory.createArrayBacked(
            StopSleeping.class, callbacks -> (entity, sleepingPos) -> {
                for (StopSleeping callback : callbacks) {
                    callback.onStopSleeping(entity, sleepingPos);
                }
            }
    );

    /**
     * An event that checks whether the current time of day is valid for sleeping.
     *
     * <p>Note that if sleeping during day time is allowed, the game will still reset the time to 0 if the usual
     * conditions are met, unless forbidden with {@link #ALLOW_RESETTING_TIME}.
     */
    public static final Event<AllowSleepTime> ALLOW_SLEEP_TIME = EventFactory.createArrayBacked(
            AllowSleepTime.class, callbacks -> (player, sleepingPos, vanillaResult) -> {
                for (AllowSleepTime callback : callbacks) {
                    InteractionResult result = callback.allowSleepTime(player, sleepingPos, vanillaResult);

                    if (result != InteractionResult.PASS) {
                        return result;
                    }
                }

                return InteractionResult.PASS;
            }
    );

    /**
     * An event that checks whether a sleeping player counts into skipping the current day and resetting the time to 0.
     *
     * <p>When this event is called, all vanilla time resetting checks have already succeeded, i.e. this event
     * is used in addition to vanilla checks.
     */
    public static final Event<AllowResettingTime> ALLOW_RESETTING_TIME = EventFactory.createArrayBacked(
            AllowResettingTime.class, callbacks -> player -> {
                for (AllowResettingTime callback : callbacks) {
                    if (!callback.allowResettingTime(player)) {
                        return false;
                    }
                }

                return true;
            }
    );

    @FunctionalInterface
    public interface StopSleeping {
        /**
         * Called when an entity stops sleeping and wakes up.
         *
         * @param entity      the sleeping entity
         * @param sleepingPos the {@linkplain LivingEntity#getSleepingPos() sleeping position} of the entity
         */
        void onStopSleeping(LivingEntity entity, BlockPos sleepingPos);
    }

    @FunctionalInterface
    public interface AllowSleepTime {
        /**
         * Checks whether the current time of day is valid for sleeping.
         *
         * <p>Non-{@linkplain InteractionResult#PASS passing} return values cancel further callbacks.
         *
         * @param player        the sleeping player
         * @param sleepingPos   the (possibly still unset) {@linkplain LivingEntity#getSleepingPos() sleeping position} of the player
         * @param vanillaResult {@code true} if vanilla allows the time, {@code false} otherwise
         *
         * @return {@link InteractionResult#SUCCESS} if the time is valid, {@link InteractionResult#FAIL} if it's not,
         * {@link InteractionResult#PASS} to fall back to other callbacks
         */
        InteractionResult allowSleepTime(Player player, BlockPos sleepingPos, boolean vanillaResult);
    }

    @FunctionalInterface
    public interface AllowResettingTime {
        /**
         * Checks whether a sleeping player counts into skipping the current day and resetting the time to 0.
         *
         * @param player the sleeping player
         *
         * @return {@code true} if allowed, {@code false} otherwise
         */
        boolean allowResettingTime(Player player);
    }
}
