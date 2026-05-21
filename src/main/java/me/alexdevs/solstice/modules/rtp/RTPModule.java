package me.alexdevs.solstice.modules.rtp;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.utils.PlayerUtils;
import me.alexdevs.solstice.modules.rtp.commands.RTPCommand;
import me.alexdevs.solstice.modules.rtp.core.Locator;
import me.alexdevs.solstice.modules.rtp.data.RTPConfig;
import me.alexdevs.solstice.modules.rtp.data.RTPLocale;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.ResourceKey;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
public class RTPModule extends ModuleBase.Toggleable {
    

    private final ArrayList<Locator> locators = new ArrayList<>();

    public RTPModule(SolsticeIdentifier id) {
        super(id);
    }

    @Override
    public void init() {
        registerLocale(RTPLocale.MODULE);
        registerConfig(RTPConfig.class, RTPConfig::new);

        commands.add(new RTPCommand(this));

        ServerTickEvents.END_SERVER_TICK.register(server -> locators.removeIf(Locator::tick));
    }

    public RTPConfig getConfig() {
        return Solstice.configManager.getData(RTPConfig.class);
    }

    public Locator createLocator(ServerPlayer player) {
        var locator = new Locator(player, PlayerUtils.getLevel(player), getConfig());
        locators.add(locator);
        return locator;
    }

    public Locator createLocatorWithBiome(ServerPlayer player, ResourceKey<Biome> biome) {
        var locator = new Locator(player, PlayerUtils.getLevel(player), getConfig(), biome);
        locators.add(locator);
        return locator;
    }
}
