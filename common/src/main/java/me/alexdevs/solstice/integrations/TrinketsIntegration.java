package me.alexdevs.solstice.integrations;

import me.alexdevs.solstice.api.platform.PlatformHelper;

public class TrinketsIntegration {
    public static boolean isAvailable() {
        return PlatformHelper.get().isModLoaded("trinkets");
    }
}
