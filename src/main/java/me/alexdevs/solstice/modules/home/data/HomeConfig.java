package me.alexdevs.solstice.modules.home.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class HomeConfig {
    @Comment("Limit how many homes a player can set. -1 means unlimited homes. Defaults to -1.")
    public int maxHomes = -1;
}
