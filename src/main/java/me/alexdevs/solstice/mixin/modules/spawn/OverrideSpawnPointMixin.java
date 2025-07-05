package me.alexdevs.solstice.mixin.modules.spawn;

import me.alexdevs.solstice.modules.ModuleProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
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

    @Shadow
    private BlockPos respawnPosition;

    @Inject(method = "findRespawnPositionAndUseSpawnBlock", at = @At("RETURN"), cancellable = true)
    public void solstice$overrideRespawnTarget(boolean keepInventory, DimensionTransition.PostDimensionTransition postDimensionTransition, CallbackInfoReturnable<DimensionTransition> cir) {
        var spawnModule = ModuleProvider.SPAWN;
        var config = spawnModule.getConfig();

        var spawn = spawnModule.getGlobalSpawnPosition();
        var world = spawn.getWorld(this.server);
        var pos = new Vec3(
                spawn.getX(),
                spawn.getY(),
                spawn.getZ()
        );

        var transition = new DimensionTransition(
                world,
                pos,
                Vec3.ZERO,
                spawn.getYaw(),
                spawn.getPitch(),
                false,
                DimensionTransition.DO_NOTHING
        );

        if (config.globalSpawn.onRespawn) {
            cir.setReturnValue(transition);
            return;
        }

        if(config.globalSpawn.onRespawnSoft && respawnPosition == null) {
            cir.setReturnValue(transition);
        }
    }
}
