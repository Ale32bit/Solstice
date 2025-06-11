package me.alexdevs.solstice.neoforge;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.platform.PlatformHelper;
import me.alexdevs.solstice.integrations.TrinketsApiProxy;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(Solstice.MOD_ID)
public class SolsticeNeoForge {
    public SolsticeNeoForge(ModContainer mod, IEventBus bus) {
        PlatformHelper.set(new NeoForgePlatform());
        TrinketsApiProxy.set(new EmptyTrinketsApi());

        new Solstice().init();
    }
}
