package me.alexdevs.solstice.modules.experiments.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class ExperimentsConfig {
    @Comment("Enable experiments. Warning: keep this module disabled in production!")
    public boolean enabled = false;
}
