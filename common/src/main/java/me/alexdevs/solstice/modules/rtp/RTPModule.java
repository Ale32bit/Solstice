package me.alexdevs.solstice.modules.rtp;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.proxy.ProxyServerTickEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.rtp.commands.RTPCommand;
import me.alexdevs.solstice.modules.rtp.core.Locator;
import me.alexdevs.solstice.modules.rtp.data.RTPConfig;
import me.alexdevs.solstice.modules.rtp.data.RTPLocale;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;

public class RTPModule extends ModuleBase.Toggleable {
    public static final String ID = "rtp";

    private final ArrayList<Locator> locators = new ArrayList<>();

    public RTPModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.localeManager.registerModule(ID, RTPLocale.MODULE);
        Solstice.configManager.registerData(ID, RTPConfig.class, RTPConfig::new);

        commands.add(new RTPCommand(this));

        ProxyServerTickEvents.END_SERVER_TICK.register(server -> locators.removeIf(Locator::tick));
    }

    public RTPConfig getConfig() {
        return Solstice.configManager.getData(RTPConfig.class);
    }

    public Locator createLocator(ServerPlayer player) {
        var locator = new Locator(player, player.serverLevel(), getConfig());
        locators.add(locator);
        return locator;
    }

    public Locator createLocatorWithBiome(ServerPlayer player, ResourceKey<Biome> biome) {
        var locator = new Locator(player, player.serverLevel(), getConfig(), biome);
        locators.add(locator);
        return locator;
    }
}
