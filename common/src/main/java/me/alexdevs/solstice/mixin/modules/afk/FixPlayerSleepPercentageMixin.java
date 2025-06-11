package me.alexdevs.solstice.mixin.modules.afk;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.afk.AfkModule;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.SleepStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SleepStatus.class)
public abstract class FixPlayerSleepPercentageMixin {
    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isSpectator()Z"))
    public boolean solstice$fixTotalPlayers(ServerPlayer player) {
        var afkModule = Solstice.modules.getModule(AfkModule.class);
        
        return player.isSpectator() || afkModule.isPlayerAfk(player);
    }
}
