package me.alexdevs.solstice.mixin.modules.spawn;

import me.alexdevs.solstice.modules.ModModuleProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//? if < 1.21.11 {
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import java.util.Optional;
//? }

@Mixin(PlayerList.class)
public abstract class OverrideNewPlayerSpawnPointMixin {
    //? if >= 1.21.1 && < 1.21.11 {
    public ServerLevel solstice$overrideWorld(MinecraftServer server, ResourceKey<Level> dimension, @Local Optional<CompoundTag> optional) {
        if (optional.isEmpty()) {
            var spawn = ModModuleProvider.SPAWN;
            var firstSpawn = spawn.getFirstSpawn();
            if (firstSpawn != null) {
                return firstSpawn.getWorld(server);
            }
            return spawn.getGlobalSpawnWorld();
        }
        return server.getLevel(dimension);
    }
    //? }
}
