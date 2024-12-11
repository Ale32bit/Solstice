package me.alexdevs.solstice.modules.near.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class NearConfig {
    @Comment("Max range in blocks. Defaults to 48 blocks.")
    public int maxRange = 48;

    @Comment("Default range in blocks. Defaults to 32 blocks.")
    public int defaultRange = 32;
}
