package me.alexdevs.solstice.mixin.modules.core;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class RealPingMixin {
    @Redirect(method = "handleKeepAlive", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerPlayer;latency:I", opcode = Opcodes.PUTFIELD))
    public void solstice$realPing(ServerPlayer player, int value, @Local int i) {
        player.latency = i;
    }
}
