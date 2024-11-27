package me.alexdevs.solstice.mixin;

import me.alexdevs.solstice.core.CustomNameFormat;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Shadow
    private MutableText addTellClickEvent(MutableText component) {
        return null;
    }

    @Shadow
    public abstract Text getName();

    @Shadow
    public abstract GameProfile getGameProfile();

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    public void getDisplayName(CallbackInfoReturnable<MutableText> cir) {
        var name = CustomNameFormat.getNameForPlayer((ServerPlayerEntity) (Object) this);
        cir.setReturnValue(addTellClickEvent(name));
    }
}
