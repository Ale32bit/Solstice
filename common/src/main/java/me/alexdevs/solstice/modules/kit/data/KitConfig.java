package me.alexdevs.solstice.modules.kit.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class KitConfig {
    @Comment("Require 'solstice.kit.kits.<kit name>' permission node by default to claim a kit.")
    public boolean requirePermission = false;
}
