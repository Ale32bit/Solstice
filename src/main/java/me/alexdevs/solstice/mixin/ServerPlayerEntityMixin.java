package me.alexdevs.solstice.mixin;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.modules.back.BackModule;
import me.alexdevs.solstice.modules.spawn.SpawnModule;
import me.alexdevs.solstice.modules.styling.formatters.DeathFormatter;
import me.alexdevs.solstice.modules.tablist.data.TabListConfig;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Shadow
    @Final
    public MinecraftServer server;

    @Inject(method = "getPlayerListName", at = @At("HEAD"), cancellable = true)
    private void solstice$customizePlayerListName(CallbackInfoReturnable<Text> callback) {
        if (Solstice.configManager.getData(TabListConfig.class).enable) {
            var player = (ServerPlayerEntity) (Object) this;
            var playerContext = PlaceholderContext.of(player);
            var text = Format.parse(Solstice.configManager.getData(TabListConfig.class).playerTabName, playerContext);
            callback.setReturnValue(text);
        }
    }

    @Redirect(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageTracker;getDeathMessage()Lnet/minecraft/text/Text;"))
    private Text solstice$getDeathMessage(DamageTracker instance) {
        var player = (ServerPlayerEntity) (Object) this;
        return DeathFormatter.onDeath(player, instance);
    }

    @Inject(method = "teleport(Lnet/minecraft/server/world/ServerWorld;DDDLjava/util/Set;FF)Z", at = @At("HEAD"))
    public void solstice$requestTeleport(ServerWorld world, double destX, double destY, double destZ, Set<PositionFlag> flags, float yaw, float pitch, CallbackInfoReturnable<Boolean> cir) {
        var player = (ServerPlayerEntity) (Object) this;
        Solstice.modules.getModule(BackModule.class).lastPlayerPositions.put(player.getUuid(), new ServerLocation(player));
    }

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
