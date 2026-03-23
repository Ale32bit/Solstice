//? if < 1.21.1 {
package me.alexdevs.solstice.mixin.modules.spawn;

import me.alexdevs.solstice.modules.ModuleProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class OverrideSpawnPointLevelMixin {

    @Shadow
    private BlockPos respawnPosition;

    @Inject(method = "getRespawnPosition", at = @At("RETURN"), cancellable = true)
    public void solstice$overrideSpawnPos(CallbackInfoReturnable<BlockPos> cir) {
        var spawnModule = ModuleProvider.SPAWN;
        var config = spawnModule.getConfig();

        var pos = spawnModule.getGlobalSpawnPosition().getBlockPos();
        if(config.globalSpawn.onRespawnSoft && this.respawnPosition == null) {
            cir.setReturnValue(pos);
        }

        if (config.globalSpawn.onRespawn) {
            cir.setReturnValue(pos);
        }
    }

    @Inject(method = "getRespawnDimension", at = @At("RETURN"), cancellable = true)
    public void solstice$overrideSpawnDimension(CallbackInfoReturnable<ResourceKey<Level>> cir) {
        var spawnModule = ModuleProvider.SPAWN;
        var config = spawnModule.getConfig();
        var levelKey = spawnModule.getGlobalSpawnPosition().getWorldKey();
        if(config.globalSpawn.onRespawnSoft && this.respawnPosition == null) {
            cir.setReturnValue(levelKey);
        }

        if (config.globalSpawn.onRespawn) {
            cir.setReturnValue(levelKey);
        }
    }
}

//? }