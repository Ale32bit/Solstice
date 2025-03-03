package me.alexdevs.solstice.api;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class Raycast {
    public static BlockHitResult cast(ServerPlayer player, double maxDistance) {
        var world = player.serverLevel();
        var eyePos = player.getEyePosition();
        var rotVec = player.getLookAngle();

        var raycastEnd = eyePos.add(rotVec.scale(maxDistance));

        var rcContext = new ClipContext(
                eyePos,
                raycastEnd,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player
        );

        return world.clip(rcContext);
    }

    public static BlockPos getBlockPos(ServerPlayer player, double maxDistance) {
        var result = cast(player, maxDistance);
        if (result.getType() == BlockHitResult.Type.BLOCK) {
            return result.getBlockPos();
        }
        return null;
    }

    public static Vec3 getEntityPos(ServerPlayer player, double maxDistance) {
        var result = cast(player, maxDistance);
        if (result.getType() == BlockHitResult.Type.ENTITY) {
            return result.getLocation();
        }
        return null;
    }

    public static Vec3 getPos(ServerPlayer player, double maxDistance) {
        var result = cast(player, maxDistance);
        if (result.getType() != BlockHitResult.Type.MISS) {
            return result.getLocation();
        }
        return null;
    }
}
