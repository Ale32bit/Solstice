package me.alexdevs.solstice.modules.rtp.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class RTPConfig {
    @Comment("This setting makes it so players have to wait before running the command a second time.")
    public boolean enableCooldown = true;

    @Comment("Seconds to wait for the cooldown to expire.")
    public int cooldown = 600;
}
