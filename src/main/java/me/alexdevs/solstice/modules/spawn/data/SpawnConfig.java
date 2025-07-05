package me.alexdevs.solstice.modules.spawn.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class SpawnConfig {

    @Comment("Require that the player has the permission of the world 'solstice.spawn.worlds.<worldName>' to warp to its spawn.\nMind that 'solstice.spawn.worlds.base' is required to be able to use the `world` argument.")
    public boolean requireWorldPermission = true;

    @Comment("This setting defines whether `/spawn` and respawning work on a per world or global server basis.")
    public GlobalSpawn globalSpawn = new GlobalSpawn();

    @ConfigSerializable
    public static class GlobalSpawn {
        @Comment("Send the player to the global spawn instead of the world spawn when using the /spawn command.")
        public boolean onSpawnCommand = false;

        @Comment("Send the player to the global spawn when respawning, respecting bed and anchor.")
        public boolean onRespawnSoft = false;

        @Comment("Send the player to the global spawn regardless of their bed or anchor when respawning.")
        public boolean onRespawn = false;

        @Comment("Send the player to the global spawn when logging in.")
        public boolean onLogin = false;

        @Comment("ID of the world to use as global spawn. Minecraft dimensions: 'minecraft:overworld', 'minecraft:the_nether', 'minecraft:the_end'")
        public String targetSpawnWorld = "minecraft:overworld";
    }
}
