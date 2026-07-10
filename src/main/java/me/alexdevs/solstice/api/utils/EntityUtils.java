package me.alexdevs.solstice.api.utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import java.util.function.Consumer;

//? >= 1.21.4
//import net.minecraft.world.entity.EntitySpawnReason;
//? < 1.21.4
import net.minecraft.world.entity.MobSpawnType;


public class EntityUtils {
    public static <T extends Entity> @Nullable T createWithCommand(
            EntityType<T> type, ServerLevel world, @Nullable Consumer<T> consumer,
            BlockPos pos, boolean particleEffects, boolean limitedLifespan) {

        final var spawnReason =
        //? >= 1.21.4
        //EntitySpawnReason.COMMAND;
        //? <1.21.4
        MobSpawnType.COMMAND;

        return type.create(world,
                consumer, pos, spawnReason, particleEffects, limitedLifespan);
    }
}
