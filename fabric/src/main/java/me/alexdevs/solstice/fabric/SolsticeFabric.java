package me.alexdevs.solstice.fabric;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.platform.PlatformHelper;
import me.alexdevs.solstice.integrations.TrinketsApiProxy;
import net.fabricmc.api.ModInitializer;

public class SolsticeFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        PlatformHelper.set(new FabricPlatform());
        TrinketsApiProxy.set(new FabricTrinketsApi());

        new Solstice().init();
    }
}
