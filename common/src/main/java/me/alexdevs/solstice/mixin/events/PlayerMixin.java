package me.alexdevs.solstice.mixin.events;

import me.alexdevs.solstice.api.events.proxy.ProxyEntitySleepEvents;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "isSleepingLongEnough", at = @At("RETURN"), cancellable = true)
    private void onIsSleepingLongEnough(CallbackInfoReturnable<Boolean> info) {
        if (info.getReturnValueZ()) {
            info.setReturnValue(ProxyEntitySleepEvents.ALLOW_RESETTING_TIME.invoker().allowResettingTime((Player) (Object) this));
        }
    }
}
