package me.alexdevs.solstice.api.platform;

import java.nio.file.Path;
import java.util.ServiceLoader;

public abstract class PlatformHelper {
    private static final PlatformHelper INSTANCE = ServiceLoader.load(PlatformHelper.class).findFirst().orElseThrow();

    public static PlatformHelper get() {
        return INSTANCE;
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
