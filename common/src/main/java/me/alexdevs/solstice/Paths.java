package me.alexdevs.solstice;

import me.alexdevs.solstice.api.platform.PlatformHelper;

import java.nio.file.Path;

public class Paths {
    public static final Path configDirectory = PlatformHelper.get().getConfigDir().resolve(Solstice.MOD_ID);
}
