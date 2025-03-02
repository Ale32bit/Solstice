package me.alexdevs.solstice.mixin.modules.miscellaneous;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.miscellaneous.MiscellaneousModule;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class BypassSleepingInBedCheckMixin {
    @Inject(method = "checkBedExists", at = @At("HEAD"), cancellable = true)
    private void solstice$bypassBedExistsCheck(CallbackInfoReturnable<Boolean> cir) {
        var module = Solstice.modules.getModule(MiscellaneousModule.class);
        if (module.isCommandSleep((LivingEntity) (Object) this)) {
            cir.setReturnValue(true);
        }
    }
}
