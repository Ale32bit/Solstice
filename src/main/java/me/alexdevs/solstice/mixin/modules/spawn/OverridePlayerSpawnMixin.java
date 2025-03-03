package me.alexdevs.solstice.mixin.modules.spawn;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.spawn.SpawnModule;
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
public abstract class OverridePlayerSpawnMixin {
    @Inject(method = "findRespawnPositionAndUseSpawnBlock", at = @At("RETURN"), cancellable = true)
    private static void solstice$overrideSpawn(ServerLevel world, BlockPos pos, float angle, boolean forced, boolean alive, CallbackInfoReturnable<Optional<Vec3>> cir) {
        var spawnModule = Solstice.modules.getModule(SpawnModule.class);
        var config = spawnModule.getConfig();
        if (config.globalSpawn.onRespawn) {
            pos = spawnModule.getGlobalSpawnPosition().getBlockPos();
            cir.setReturnValue(Optional.of(pos.getCenter()));
        }
    }
}
