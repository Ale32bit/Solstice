package me.alexdevs.solstice.mixin.modules.core;
import com.llamalad7.mixinextras.sugar.Local;
//? if >= 1.21.1 {
/*import net.minecraft.server.network.ServerCommonPacketListenerImpl;*/
//? } else {
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
//? }
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
//? if >= 1.21.1 {
/*import org.spongepowered.asm.mixin.Shadow;*/
//? }
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
//? if >= 1.21.1 {
/*@Mixin(ServerCommonPacketListenerImpl.class)*/
//? } else {
@Mixin(ServerGamePacketListenerImpl.class)
//? }
public abstract class RealPingMixin {
    //? if >= 1.21.1 {
    /*@Shadow
    private int latency;
    @Redirect(method = "handleKeepAlive", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerCommonPacketListenerImpl;latency:I", opcode = Opcodes.PUTFIELD))
    public void solstice$realPing(ServerCommonPacketListenerImpl instance, int value, @Local int i) {
        latency = i;
    }*/
    //? } else {
    @Redirect(method = "handleKeepAlive", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerPlayer;latency:I", opcode = Opcodes.PUTFIELD))
    public void solstice$realPing(ServerPlayer player, int value, @Local int i) {
        player.latency = i;
    }
    //? }
}
