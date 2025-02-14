package me.alexdevs.solstice.mixin.modules.core;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class RealPingMixin {
    @Redirect(method = "onKeepAlive", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayerEntity;pingMilliseconds:I", opcode = Opcodes.PUTFIELD))
    public void solstice$realPing(ServerPlayerEntity player, int value, @Local int i) {
        player.pingMilliseconds = i;
    }
}
