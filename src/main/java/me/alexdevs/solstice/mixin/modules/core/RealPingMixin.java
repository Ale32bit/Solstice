package me.alexdevs.solstice.mixin.modules.core;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerCommonNetworkHandler.class)
public abstract class RealPingMixin {
    @Shadow
    private int latency;

    @Redirect(method = "onKeepAlive", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerCommonNetworkHandler;latency:I", opcode = Opcodes.PUTFIELD))
    public void solstice$realPing(ServerCommonNetworkHandler handler, int value, @Local int i) {
        latency = i;
    }
}
