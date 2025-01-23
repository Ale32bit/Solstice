package me.alexdevs.solstice;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class Paths {
    public static final Path configDirectory = FabricLoader.getInstance().getConfigDir().resolve(Solstice.MOD_ID);
}
