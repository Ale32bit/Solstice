package me.alexdevs.solstice.modules.broadcast.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class BroadcastConfig {
    @Comment("Format to use when broadcasting a message.")
    public String format = "<red>[</red><gold>Broadcast</gold><red>]</red> ${message}";
}
