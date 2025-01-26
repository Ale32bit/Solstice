package me.alexdevs.solstice.modules.teleportRequest.data;

import net.minecraft.server.network.ServerPlayerEntity;

public class Request {
    public enum Direction {
        SOURCE_TO_TARGET,
        TARGET_TO_SOURCE,
    }

    private final ServerPlayerEntity source;
    private int remainingTime;
    private final Direction direction;

    public Request(ServerPlayerEntity source, int remainingTime, Direction direction) {
        this.source = source;
        this.remainingTime = remainingTime;
        this.direction = direction;
    }

    public ServerPlayerEntity getSource() {
        return source;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public boolean tickDown() {
        return remainingTime-- <= 0;
    }

    public Direction getDirection() {
        return direction;
    }

}
