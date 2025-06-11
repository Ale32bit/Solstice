package me.alexdevs.solstice.mixin.modules.customname;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.customName.CustomNameModule;
import net.minecraft.network.chat.Component;
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
    protected abstract MutableComponent decorateDisplayNameComponent(MutableComponent component);

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    public void solstice$getDisplayName(CallbackInfoReturnable<Component> cir) {
        var customNameModule = Solstice.modules.getModule(CustomNameModule.class);
        var name = customNameModule.getNameForPlayer((ServerPlayer) (Object) this);
        cir.setReturnValue(decorateDisplayNameComponent(name));
    }
}
