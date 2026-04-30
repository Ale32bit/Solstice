package me.alexdevs.solstice.api.utils;

import net.minecraft.resources.ResourceLocation;

public class ResourceUtils {

    public static ResourceLocation location(String namespace, String path) {
        //? if >= 1.21.1
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
        //? if < 1.21.1
        //return new ResourceLocation(namespace, path);
    }

    public static ResourceLocation parse(String value) {
        //? if >= 1.21.1
        return ResourceLocation.parse(value);
        //? if < 1.21.1
        //return ResourceLocation.tryParse(value);
    }
}

