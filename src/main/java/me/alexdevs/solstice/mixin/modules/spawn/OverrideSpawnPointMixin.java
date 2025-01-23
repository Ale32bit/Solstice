package me.alexdevs.solstice.mixin.modules.spawn;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.spawn.SpawnModule;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class OverrideSpawnPointMixin {
    @Shadow
    @Final
    public MinecraftServer server;

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

    @Inject(method = "getRespawnTarget", at = @At("RETURN"), cancellable = true)
    public void solstice$overrideRespawnTarget(boolean alive, TeleportTarget.PostDimensionTransition postDimensionTransition, CallbackInfoReturnable<TeleportTarget> cir) {
        var spawnModule = Solstice.modules.getModule(SpawnModule.class);
        var config = spawnModule.getConfig();
        if (config.globalSpawn.onRespawn) {
            var spawn = spawnModule.getGlobalSpawnPosition();

            var world = spawn.getWorld(this.server);
            var pos = new Vec3d(
                    spawn.getX(),
                    spawn.getY(),
                    spawn.getZ()
            );

            cir.setReturnValue(new TeleportTarget(
                    world,
                    pos,
                    Vec3d.ZERO,
                    spawn.getYaw(),
                    spawn.getPitch(),
                    false,
                    TeleportTarget.NO_OP
            ));
        }
    }
}
