package me.alexdevs.solstice.mixin.modules.spawn;
import me.alexdevs.solstice.modules.ModuleProvider;
import net.minecraft.core.BlockPos;
//? if >= 1.21.1 {
/*import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;*/
//? } else {
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
//? }
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if < 1.21.1 {
import java.util.Optional;
//? }
//? if >= 1.21.1 {
/*@Mixin(ServerPlayer.class)*/
//? } else {
@Mixin(Player.class)
//? }
public abstract class OverrideSpawnPointMixin {
    //? if >= 1.21.1 {
    /*@Shadow @Final public MinecraftServer server;
    @Shadow private BlockPos respawnPosition;
    @Inject(method = "findRespawnPositionAndUseSpawnBlock", at = @At("RETURN"), cancellable = true)
    public void solstice$overrideRespawnTarget(boolean keepInventory, DimensionTransition.PostDimensionTransition postDimensionTransition, CallbackInfoReturnable<DimensionTransition> cir) {
        var spawnModule = ModuleProvider.SPAWN;
        var config = spawnModule.getConfig();
        var spawn = spawnModule.getGlobalSpawnPosition();
        var world = spawn.getWorld(this.server);
        var pos = new Vec3(spawn.getX(), spawn.getY(), spawn.getZ());
        var transition = new DimensionTransition(
                world, pos, Vec3.ZERO,
                spawn.getYaw(), spawn.getPitch(),
                false, DimensionTransition.DO_NOTHING
        );
        if (config.globalSpawn.onRespawn) {
            cir.setReturnValue(transition);
            return;
        }
        if (config.globalSpawn.onRespawnSoft && respawnPosition == null) {
            cir.setReturnValue(transition);
        }
    }*/
    //? } else {
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
    //? }
}
