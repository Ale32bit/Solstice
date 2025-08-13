package me.alexdevs.solstice.mixin.modules.spawn;

import me.alexdevs.solstice.modules.ModuleProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Player.class)
public abstract class OverrideSpawnPointMixin {
    @Inject(method = "findRespawnPositionAndUseSpawnBlock", at = @At("RETURN"), cancellable = true)
    private static void solstice$overrideSpawnPos(ServerLevel level, BlockPos pos, float angle, boolean forced, boolean alive, CallbackInfoReturnable<Optional<Vec3>> cir) {
        var spawnModule = ModuleProvider.SPAWN;
        var config = spawnModule.getConfig();

        var spawn = spawnModule.getGlobalSpawnPosition();
        if (config.globalSpawn.onRespawnSoft && pos == null) {
            pos = spawn.getBlockPos();
            cir.setReturnValue(Optional.of(pos.getCenter()));
        }

        if (config.globalSpawn.onRespawn) {
            pos = spawn.getBlockPos();
            cir.setReturnValue(Optional.of(pos.getCenter()));
        }
    }
}
