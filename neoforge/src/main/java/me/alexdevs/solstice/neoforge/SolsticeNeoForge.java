package me.alexdevs.solstice.neoforge;

import me.alexdevs.solstice.Solstice;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(Solstice.MOD_ID)
public class SolsticeNeoForge {
    public SolsticeNeoForge(ModContainer mod, IEventBus bus) {
        new Solstice().init();
    }
}
