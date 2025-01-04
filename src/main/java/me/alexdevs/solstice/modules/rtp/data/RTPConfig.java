package me.alexdevs.solstice.modules.rtp.data;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.List;

@ConfigSerializable
public class RTPConfig {
    @Comment("How many times to try to find a valid spot to teleport to before failing.")
    public int attempts = 10;

    @Comment("How much time in milliseconds /rtp can take at most before failing.")
    public int timeout = 10000;

    @Comment("Minimum radius from the center of the world border.")
    public int minRadius = 0;

    @Comment("Maximum radius from the center of the world border. It caps to world border size.")
    public int maxRadius = 30000;

    @Comment("Use player as the center of the radius instead of the world border.")
    public boolean aroundPlayer = false;

    @Comment("List of biomes an attempt should fail at.")
    public List<String> prohibitedBiomes = List.of(
            "minecraft:ocean",
            "minecraft:cold_ocean",
            "minecraft:deep_cold_ocean",
            "minecraft:deep_frozen_ocean",
            "minecraft:deep_lukewarm_ocean",
            "minecraft:deep_ocean",
            "minecraft:frozen_ocean",
            "minecraft:lukewarm_ocean",
            "minecraft:warm_ocean",
            "minecraft:river",
            "minecraft:frozen_river",
            "minecraft:small_end_islands"
    );

    @Comment("Require that the player has the permission of the world 'solstice.rtp.worlds.<worldName>' to initiate the random teleport in the world.")
    public boolean requireWorldPermission = true;

    @Comment("Cooldown configuration")
    public Cooldown cooldown = new Cooldown();

    @ConfigSerializable
    public static class Cooldown {
        @Comment("This setting makes it so players have to wait before running the command a second time.")
        public boolean enable = true;

        @Comment("Seconds to wait for the cooldown to expire.")
        public int cooldown = 600;

        @Comment("Cancel the cooldown if /rtp fails.")
        public boolean cancelOnFail = true;
    }



    public List<RegistryKey<Biome>> parseBiomes() {
        return prohibitedBiomes.stream().map(biomeId -> RegistryKey.of(RegistryKeys.BIOME, new Identifier(biomeId))).toList();
    }
}
