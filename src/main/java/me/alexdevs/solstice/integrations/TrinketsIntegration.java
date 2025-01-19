package me.alexdevs.solstice.integrations;

import net.fabricmc.loader.api.FabricLoader;

public class TrinketsIntegration {
    public static boolean isAvailable() {
        return FabricLoader.getInstance().isModLoaded("trinkets");
    }
}
