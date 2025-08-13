package me.alexdevs.solstice.mixin.modules.spawn;

import com.llamalad7.mixinextras.sugar.Local;
import me.alexdevs.solstice.modules.ModuleProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(PlayerList.class)
public abstract class OverrideNewPlayerSpawnPointMixin {
    // Lnet/minecraft/server/MinecraftServer;getLevel(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/server/level/ServerLevel;
    @Redirect(
            method = "placeNewPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;getLevel(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/server/level/ServerLevel;"
            )
    )
    public ServerLevel solstice$overrideWorld(MinecraftServer server, ResourceKey<Level> dimension) {
        var level = server.getLevel(dimension);
        if (level == null) {
            var spawn = ModuleProvider.SPAWN;
            var firstSpawn = spawn.getFirstSpawn();
            if (firstSpawn != null) {
                return firstSpawn.getWorld(server);
            }
            return spawn.getGlobalSpawnWorld();
        }
        return server.getLevel(dimension);
    }
}
