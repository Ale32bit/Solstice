package me.alexdevs.solstice.modules.home.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.Map;

@ConfigSerializable
public class HomeConfig {

    @Comment("The amount of homes a player can set based on their permission group. <group> = <max homes>.\nUse the permission 'solstice.home.unlimited' to bypass this limit.")
    public Map<String, Integer> homes = Map.of(
            "default", 5,
            "vip", 10
    );
}
