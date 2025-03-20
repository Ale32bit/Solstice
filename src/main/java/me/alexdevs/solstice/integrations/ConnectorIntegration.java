package me.alexdevs.solstice.integrations;

import me.alexdevs.solstice.Solstice;
import net.fabricmc.loader.api.FabricLoader;

public class ConnectorIntegration {
    public static final String CONNECTOR_ID = "connectormod";
    private static boolean isForge = false;

    public static void register() {
        var optContainer = FabricLoader.getInstance().getModContainer(CONNECTOR_ID);
        if (optContainer.isPresent()) {
            isForge = true;
            Solstice.LOGGER.warn("Sinytra connector detected. Support may be limited!");
        }
    }

    public static boolean isForge() {
        return isForge;
    }
}
