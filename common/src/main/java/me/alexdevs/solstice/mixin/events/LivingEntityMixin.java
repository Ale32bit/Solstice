package me.alexdevs.solstice.mixin.events;

import me.alexdevs.solstice.api.events.proxy.ProxyEntitySleepEvents;
import me.alexdevs.solstice.api.events.proxy.ProxyServerLivingEntityEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    public abstract Optional<BlockPos> getSleepingPos();

    @Inject(
            method = "die", at = @At(
            value = "INVOKE",
            target = "net/minecraft/world/level/Level.broadcastEntityEvent(Lnet/minecraft/world/entity/Entity;B)V"
    )
    )
    private void notifyDeath(DamageSource source, CallbackInfo ci) {
        ProxyServerLivingEntityEvents.AFTER_DEATH.invoker().afterDeath((LivingEntity) (Object) this, source);
    }

    @Inject(method = "stopSleeping", at = @At("HEAD"))
    private void onWakeUp(CallbackInfo info) {
        BlockPos sleepingPos = getSleepingPos().orElse(null);

        // If actually asleep - this method is often called with data loading, syncing etc. "just to be sure"
        if (sleepingPos != null) {
            ProxyEntitySleepEvents.STOP_SLEEPING.invoker().onStopSleeping((LivingEntity) (Object) this, sleepingPos);
        }
    }
}
