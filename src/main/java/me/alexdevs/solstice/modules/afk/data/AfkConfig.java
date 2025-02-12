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

    @Comment("These triggers clear the AFK status. Events regarding entities, blocks or item usage may be triggered by fake players.")
    public AfkTriggers triggers = new AfkTriggers();

    @ConfigSerializable
    public static class AfkTriggers {
        @Comment("Movement is triggered when the velocity threshold is met.")
        public boolean onMovement = true;

        @Comment("Look change is triggered when the player yaw and/or pitch change.")
        public boolean onLookChange = true;

        @Comment("Trigger on chat messages sent by the player.")
        public boolean onChat = true;

        @Comment("Trigger on commands.")
        public boolean onCommand = true;

        @Comment("Trigger when a block is being attacked (left click).")
        public boolean onBlockAttack = true;

        @Comment("Trigger when a block is being interacted with (right click).")
        public boolean onBlockInteract = true;

        @Comment("Trigger when an entity is attacked.")
        public boolean onEntityAttack = true;

        @Comment("Trigger when an entity is interacted with.")
        public boolean onEntityInteract = true;

        @Comment("Trigger when an item is used.")
        public boolean onItemUse = true;
    }
}
