package me.alexdevs.solstice.modules.info.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class InfoConfig {
    @Comment("Send the 'Message Of The Day' to the player when joining the server. Content is in the 'config/solstice/info/motd.txt' file.")
    public boolean enableMotd = true;
}
