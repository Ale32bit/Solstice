package me.alexdevs.solstice.modules.help.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class HelpConfig {

    @Comment( "The page size for the help command. Default: 10.")
    public int pageSize = 10;
}
