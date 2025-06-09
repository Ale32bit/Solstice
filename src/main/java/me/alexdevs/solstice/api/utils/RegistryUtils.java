package me.alexdevs.solstice.api.utils;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public class RegistryUtils {
    public static List<String> getBiomes(HolderLookup.RegistryLookup<Biome> registry, boolean includeTags) {
        var biomes = new ArrayList<>(registry.listElementIds().map(q -> q.location().toString()).toList());
        if (includeTags) {
            biomes.addAll(registry.listTagIds().map(q -> '#' + q.location().toString()).toList());
        }

        return biomes;
    }
}
