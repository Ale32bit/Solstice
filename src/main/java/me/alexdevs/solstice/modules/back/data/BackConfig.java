package me.alexdevs.solstice.modules.back.data;

import me.alexdevs.solstice.api.ServerLocation;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class BackConfig {
    @Comment("Keep the location of players' back location saved between restarts and player reconnections.")
    public boolean persistLocation = true;

    @Comment("Clear the /back location of an online player after the configured seconds. Set to -1 to disable. Default: -1")
    public int clearTimeout = -1;

    @Comment("Clear the /back location of an offline player after the configured seconds. Set to -1 to disable. Default: 300 (5 minutes)")
    public int offlineClearTimeout = -1;

    // It's actually a cuboid and the "range" is the length
    @Comment("Range for the safe position checks.")
    public int safeCheckRange = ServerLocation.SAFE_RANGE;
}
