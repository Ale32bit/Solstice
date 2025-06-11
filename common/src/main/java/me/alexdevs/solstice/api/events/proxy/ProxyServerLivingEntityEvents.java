package me.alexdevs.solstice.api.events.proxy;

import me.alexdevs.solstice.api.events.backend.Event;
import me.alexdevs.solstice.api.events.backend.EventFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class ProxyServerLivingEntityEvents {
    /**
     * An event that is called when a living entity dies.
     */
    public static final Event<AfterDeath> AFTER_DEATH = EventFactory.createArrayBacked(
            AfterDeath.class, callbacks -> (entity, damageSource) -> {
                for (AfterDeath callback : callbacks) {
                    callback.afterDeath(entity, damageSource);
                }
            }
    );

    @FunctionalInterface
    public interface AfterDeath {
        /**
         * Called when a living entity dies. The death cannot be canceled at this point.
         *
         * @param entity       the entity
         * @param damageSource the source of the fatal damage
         */
        void afterDeath(LivingEntity entity, DamageSource damageSource);
    }
}
