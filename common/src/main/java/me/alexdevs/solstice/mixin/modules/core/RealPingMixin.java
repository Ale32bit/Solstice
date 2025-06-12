package me.alexdevs.solstice.mixin.modules.core;

import com.llamalad7.mixinextras.sugar.Local;
import me.alexdevs.solstice.core.coreModule.CoreModule;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerCommonPacketListenerImpl.class)
public abstract class RealPingMixin {
    @Shadow private int latency;

    @Redirect(
            method = "handleKeepAlive", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/server/network/ServerCommonPacketListenerImpl;latency:I",
            opcode = Opcodes.PUTFIELD
    )
    )
    public void solstice$realPing(ServerCommonPacketListenerImpl instance, int value, @Local int i) {
        if (!CoreModule.getConfig().useRealPing) return;

        latency = i;
    }
}
