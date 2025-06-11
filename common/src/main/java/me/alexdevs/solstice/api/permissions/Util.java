package me.alexdevs.solstice.api.permissions;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

class Util {
    static CommandSourceStack commandSourceFromEntity(Entity entity) {
        if (entity instanceof ServerPlayer) {
            return entity.createCommandSourceStack();
        }
        Level world = entity.level();
        if (world instanceof ServerLevel) {
//            return entity.createCommandSourceStack((ServerLevel) world);
            return entity.createCommandSourceStack();
        } else {
            throw new IllegalArgumentException("Entity '" +
                                               entity +
                                               "' is not a server entity. Try passing a CommandSource directly instead.");
        }
    }
}
