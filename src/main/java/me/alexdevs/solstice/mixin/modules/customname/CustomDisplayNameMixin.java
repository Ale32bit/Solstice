package me.alexdevs.solstice.mixin.modules.customname;

import me.alexdevs.solstice.modules.ModModuleProvider;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class CustomDisplayNameMixin {
    @Shadow
    private MutableComponent decorateDisplayNameComponent(MutableComponent component) {
        return null;
    }

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    public void solstice$getDisplayName(CallbackInfoReturnable<MutableComponent> cir) {
        var name = ModModuleProvider.CUSTOMNAME.getNameForPlayer((ServerPlayer) (Object) this);
        cir.setReturnValue(decorateDisplayNameComponent(name));
    }
}
