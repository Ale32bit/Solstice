package me.alexdevs.solstice.api;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;

import java.util.function.Predicate;

public class Permission {
    public final String node;
    public final boolean defaultValue;
    public final int defaultLevel;

    public Permission(String node) {
        this.node = node;
        this.defaultValue = false;
        this.defaultLevel = -1;
    }

    public Permission(String node, boolean grantDefault) {
        this.node = node;
        this.defaultValue = grantDefault;
        this.defaultLevel = -1;
    }

    public Permission(String node, int defaultLevel) {
        this.node = node;
        this.defaultValue = false;
        this.defaultLevel = defaultLevel;
    }

    public Predicate<ServerCommandSource> getRequirement() {
        if (this.defaultLevel >= 0)
            return Permissions.require(this.node, this.defaultLevel);
        else
            return Permissions.require(this.node, this.defaultValue);
    }
}
