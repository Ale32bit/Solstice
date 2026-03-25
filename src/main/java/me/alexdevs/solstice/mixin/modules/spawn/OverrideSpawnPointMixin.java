package me.alexdevs.solstice.mixin.modules.spawn;
import me.alexdevs.solstice.modules.ModuleProvider;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if >= 1.21.1 {
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
//? }

//? if >= 1.21.4 {
/*import net.minecraft.world.level.portal.TeleportTransition;
*///? } else if >= 1.21.1 {
import net.minecraft.world.level.portal.DimensionTransition;
//? } else {
/*import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import java.util.Optional;
*///? }

//? >= 1.21.1
@Mixin(ServerPlayer.class)
//? < 1.21.1
//@Mixin(Player.class)
public abstract class OverrideSpawnPointMixin {
    //? if >= 1.21.11 {
    /*@Shadow @Final public MinecraftServer server;
    @Shadow private ServerPlayer.RespawnConfig respawnConfig;
    @Inject(method = "findRespawnPositionAndUseSpawnBlock", at = @At("RETURN"), cancellable = true)
    public void solstice$overrideRespawnTarget(boolean useCharge, TeleportTransition.PostTeleportTransition postTeleportTransition, CallbackInfoReturnable<Object> cir) {
        var spawnModule = ModuleProvider.SPAWN;
        var config = spawnModule.getConfig();
        var spawn = spawnModule.getGlobalSpawnPosition();
        var world = spawn.getWorld(this.server);
        var pos = new Vec3(spawn.getX(), spawn.getY(), spawn.getZ());
        var transition = new TeleportTransition(
                world, pos, Vec3.ZERO,
                spawn.getYaw(), spawn.getPitch(),
                TeleportTransition.DO_NOTHING
        );
        if (config.globalSpawn.onRespawn) {
            cir.setReturnValue(transition);
            return;
        }
        if (config.globalSpawn.onRespawnSoft && respawnConfig == null) {
            cir.setReturnValue(transition);
        }
    }
    *///? } else if >= 1.21.4 {
    /*@Shadow @Final public MinecraftServer server;
    @Shadow private BlockPos respawnPosition;
    @Inject(method = "findRespawnPositionAndUseSpawnBlock", at = @At("RETURN"), cancellable = true)
    public void solstice$overrideRespawnTarget(boolean useCharge, TeleportTransition.PostTeleportTransition postTeleportTransition, CallbackInfoReturnable<Object> cir) {
        var spawnModule = ModuleProvider.SPAWN;
        var config = spawnModule.getConfig();
        var spawn = spawnModule.getGlobalSpawnPosition();
        var world = spawn.getWorld(this.server);
        var pos = new Vec3(spawn.getX(), spawn.getY(), spawn.getZ());
        var transition = new TeleportTransition(
                world, pos, Vec3.ZERO,
                spawn.getYaw(), spawn.getPitch(),
                TeleportTransition.DO_NOTHING
        );
        if (config.globalSpawn.onRespawn) {
            cir.setReturnValue(transition);
            return;
        }
        if (config.globalSpawn.onRespawnSoft && respawnPosition == null) {
            cir.setReturnValue(transition);
        }
    }
    *///? } else if >= 1.21.1 {
    @Shadow @Final public MinecraftServer server;
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
    }
    //? } else {
    /*@Inject(method = "findRespawnPositionAndUseSpawnBlock", at = @At("RETURN"), cancellable = true)
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
    *///? }
}
