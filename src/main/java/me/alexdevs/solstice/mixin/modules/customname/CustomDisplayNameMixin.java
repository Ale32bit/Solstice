package me.alexdevs.solstice.mixin.modules.customname;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.customName.CustomNameModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class CustomDisplayNameMixin {
    @Shadow
    private MutableText addTellClickEvent(MutableText component) {
        return null;
    }

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    public void solstice$getDisplayName(CallbackInfoReturnable<MutableText> cir) {
        var customNameModule = Solstice.modules.getModule(CustomNameModule.class);
        var name = customNameModule.getNameForPlayer((ServerPlayerEntity) (Object) this);
        cir.setReturnValue(addTellClickEvent(name));
    }
}
