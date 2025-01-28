package me.alexdevs.solstice.mixin.modules.afk;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.afk.AfkModule;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.SleepManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SleepManager.class)
public abstract class FixPlayerSleepPercentageMixin {
    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;isSpectator()Z"))
    public boolean solstice$fixTotalPlayers(ServerPlayerEntity player) {
        var afkModule = Solstice.modules.getModule(AfkModule.class);
        
        return player.isSpectator() || afkModule.isPlayerAfk(player);
    }
}
