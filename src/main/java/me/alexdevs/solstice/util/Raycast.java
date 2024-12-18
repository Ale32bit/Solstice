package me.alexdevs.solstice.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class Raycast {
    public static BlockHitResult cast(ServerPlayerEntity player, double maxDistance) {
        var world = player.getServerWorld();
        var eyePos = player.getEyePos();
        var rotVec = player.getRotationVector();

        var raycastEnd = eyePos.add(rotVec.multiply(maxDistance));

        var rcContext = new RaycastContext(
                eyePos,
                raycastEnd,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                player
        );

        return world.raycast(rcContext);
    }

    public static BlockPos getBlockPos(ServerPlayerEntity player, double maxDistance) {
        var result = cast(player, maxDistance);
        if(result.getType() == BlockHitResult.Type.BLOCK) {
            return result.getBlockPos();
        }
        return null;
    }

    public static Vec3d getEntityPos(ServerPlayerEntity player, double maxDistance) {
        var result = cast(player, maxDistance);
        if(result.getType() == BlockHitResult.Type.ENTITY) {
            return result.getPos();
        }
        return null;
    }

    public static Vec3d getPos(ServerPlayerEntity player, double maxDistance) {
        var result = cast(player, maxDistance);
        if(result.getType() != BlockHitResult.Type.MISS) {
            return result.getPos();
        }
        return null;
    }
}
