package me.alexdevs.solstice.integrations;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.platform.PlatformHelper;

public class ConnectorIntegration {
    public static final String CONNECTOR_ID = "connector";
    private static boolean isForge = false;

    public static void register() {
        if (PlatformHelper.get().isNativeForge()) {
            isForge = true;
        } else {
            if (PlatformHelper.get().isModLoaded(CONNECTOR_ID)) {
                isForge = true;
                Solstice.LOGGER.warn("Sinytra connector detected. Support may be limited!");
            }
        }
    }

    public static boolean isForge() {
        return isForge;
    }
}
