package me.alexdevs.solstice.modules.teleportRequest.data;

import net.minecraft.server.level.ServerPlayer;

public class Request {
    public enum Direction {
        SOURCE_TO_TARGET,
        TARGET_TO_SOURCE,
    }

    private final ServerPlayer source;
    private int remainingTime;
    private final Direction direction;

    public Request(ServerPlayer source, int remainingTime, Direction direction) {
        this.source = source;
        this.remainingTime = remainingTime;
        this.direction = direction;
    }

    public ServerPlayer getSource() {
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
