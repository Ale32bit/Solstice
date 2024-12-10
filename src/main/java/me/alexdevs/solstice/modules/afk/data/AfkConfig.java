package me.alexdevs.solstice.modules.afk.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class AfkConfig {
    @Comment("Enable the AFK functionality. Requires server restart.")
    public boolean enable = true;

    @Comment("Announce in chat when a player goes or return from AFK.")
    public boolean announce = true;

    @Comment("AFK triggers after the player has been inactive for the following seconds. Defaults to 300 seconds.")
    public int timeTrigger = 300;

    @Comment("This tag is displayed with `solstice:afk` placeholder when the player is AFK.")
    public String tag = "<gray>[AFK]</gray> ";
}
