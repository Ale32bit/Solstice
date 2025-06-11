package me.alexdevs.solstice.api.platform;

import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;

public abstract class PlatformHelper {
    private static PlatformHelper INSTANCE;

    public static PlatformHelper get() {
        return INSTANCE;
    }

    @ApiStatus.Internal
    public static void set(PlatformHelper helper) {
        INSTANCE = helper;
    }

    public abstract void init();

    public abstract Path getGameDir();

    public abstract Path getConfigDir();

    public abstract boolean isModLoaded(String id);

    public abstract boolean isNativeForge();

    public abstract Object getModContainer();

    public abstract String getModVersion();

    public abstract ModInfo getModInfo(String id);
}
