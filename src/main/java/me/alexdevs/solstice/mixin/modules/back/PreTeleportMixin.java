package me.alexdevs.solstice.mixin.modules.back;

import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.modules.ModuleProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Relative;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(ServerPlayer.class)
public abstract class PreTeleportMixin {
    @Inject(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FFZ)Z", at = @At("HEAD"))
    public void solstice$getPreTeleportLocation(ServerLevel world, double destX, double destY, double destZ, Set<Relative> flags, float yaw, float pitch, boolean setCamera, CallbackInfoReturnable<Boolean> cir) {
        var player = (ServerPlayer) (Object) this;
        ModuleProvider.BACK.setPlayerLastLocation(player.getUUID(), new ServerLocation(player));
    }

}
