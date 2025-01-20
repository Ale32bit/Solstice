package me.alexdevs.solstice.mixin.modules.spawn;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.spawn.SpawnModule;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class OverrideSpawnPointMixin {
    @Inject(method = "getSpawnPointPosition", at = @At("RETURN"), cancellable = true)
    public void solstice$overrideSpawnPos(CallbackInfoReturnable<BlockPos> cir) {
        var spawnModule = Solstice.modules.getModule(SpawnModule.class);
        var config = spawnModule.getConfig();
        if (config.globalSpawn.onRespawn) {
            var pos = spawnModule.getGlobalSpawnPosition().getBlockPos();
            cir.setReturnValue(pos);
        }
    }

    @Inject(method = "getSpawnPointDimension", at = @At("RETURN"), cancellable = true)
    public void solstice$overrideSpawnDimension(CallbackInfoReturnable<RegistryKey<World>> cir) {
        var spawnModule = Solstice.modules.getModule(SpawnModule.class);
        var config = spawnModule.getConfig();
        if (config.globalSpawn.onRespawn) {
            cir.setReturnValue(spawnModule.getGlobalSpawnWorld().getRegistryKey());
        }
    }
}
