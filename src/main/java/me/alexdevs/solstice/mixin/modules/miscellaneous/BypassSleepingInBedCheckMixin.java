package me.alexdevs.solstice.mixin.modules.miscellaneous;

import me.alexdevs.solstice.modules.ModModuleProvider;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class BypassSleepingInBedCheckMixin {
    @Inject(method = "checkBedExists", at = @At("HEAD"), cancellable = true)
    private void isSleepingInBed(CallbackInfoReturnable<Boolean> cir) {
        if (ModModuleProvider.MISCELLANEOUS.isCommandSleep((LivingEntity) (Object) this)) {
            cir.setReturnValue(true);
        }
    }
}
