package me.alexdevs.solstice.fabric;

import me.alexdevs.solstice.Solstice;
import net.fabricmc.api.ModInitializer;

public class SolsticeFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        new Solstice().init();
    }
}
