package me.alexdevs.solstice.modules.spawn.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class SpawnConfig {

    @Comment("Send the player to spawn after respawning from death. This setting ignores other spawnpoints such as beds and respawn anchors.")
    public boolean forceOnDeath = true;
}
