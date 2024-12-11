package me.alexdevs.solstice.modules.teleport.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class TeleportConfig {
    @Comment("The teleport request times out after the following seconds. Defaults to 120 seconds.")
    public int teleportRequestTimeout = 120;
}
