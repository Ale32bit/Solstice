package me.alexdevs.solstice.mixin.modules.spawn;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.ModuleProvider;
import me.alexdevs.solstice.modules.spawn.SpawnModule;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class OverrideSpawnPointMixin {
    @Shadow
    @Final
    public MinecraftServer server;

    @Inject(method = "getRespawnPosition", at = @At("RETURN"), cancellable = true)
    public void solstice$overrideSpawnPos(CallbackInfoReturnable<BlockPos> cir) {
        var spawnModule = ModuleProvider.SPAWN;
        var config = spawnModule.getConfig();
        if (config.globalSpawn.onRespawn) {
            var pos = spawnModule.getGlobalSpawnPosition().getBlockPos();
            cir.setReturnValue(pos);
        }
    }

    @Inject(method = "getRespawnDimension", at = @At("RETURN"), cancellable = true)
    public void solstice$overrideSpawnDimension(CallbackInfoReturnable<ResourceKey<Level>> cir) {
        var spawnModule = ModuleProvider.SPAWN;
        var config = spawnModule.getConfig();
        if (config.globalSpawn.onRespawn) {
            cir.setReturnValue(spawnModule.getGlobalSpawnWorld().dimension());
        }
    }

    @Inject(method = "findRespawnPositionAndUseSpawnBlock", at = @At("RETURN"), cancellable = true)
    public void solstice$overrideRespawnTarget(boolean keepInventory, DimensionTransition.PostDimensionTransition postDimensionTransition, CallbackInfoReturnable<DimensionTransition> cir) {
        var spawnModule = ModuleProvider.SPAWN;
        var config = spawnModule.getConfig();
        if (config.globalSpawn.onRespawn) {
            var spawn = spawnModule.getGlobalSpawnPosition();

            var world = spawn.getWorld(this.server);
            var pos = new Vec3(
                    spawn.getX(),
                    spawn.getY(),
                    spawn.getZ()
            );

            cir.setReturnValue(new DimensionTransition(
                    world,
                    pos,
                    Vec3.ZERO,
                    spawn.getYaw(),
                    spawn.getPitch(),
                    false,
                    DimensionTransition.DO_NOTHING
            ));
        }
    }
}
