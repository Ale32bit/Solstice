package me.alexdevs.solstice.mixin.modules.spawn;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.spawn.SpawnModule;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class OverrideSpawnPointMixin {
    @Inject(method = "getRespawnPosition", at = @At("RETURN"), cancellable = true)
    public void solstice$overrideSpawnPos(CallbackInfoReturnable<BlockPos> cir) {
        var spawnModule = Solstice.modules.getModule(SpawnModule.class);
        var config = spawnModule.getConfig();
        if (config.globalSpawn.onRespawn) {
            var pos = spawnModule.getGlobalSpawnPosition().getBlockPos();
            cir.setReturnValue(pos);
        }
    }

    @Inject(method = "getRespawnDimension", at = @At("RETURN"), cancellable = true)
    public void solstice$overrideSpawnDimension(CallbackInfoReturnable<ResourceKey<Level>> cir) {
        var spawnModule = Solstice.modules.getModule(SpawnModule.class);
        var config = spawnModule.getConfig();
        if (config.globalSpawn.onRespawn) {
            cir.setReturnValue(spawnModule.getGlobalSpawnWorld().dimension());
        }
    }
}
