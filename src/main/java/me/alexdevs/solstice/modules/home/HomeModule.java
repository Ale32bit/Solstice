package me.alexdevs.solstice.modules.home;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleProperties;
import me.alexdevs.solstice.modules.home.commands.*;
import me.alexdevs.solstice.modules.home.data.HomeConfig;
import me.alexdevs.solstice.modules.home.data.HomeLocale;
import me.alexdevs.solstice.modules.home.data.HomePlayerData;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;

import java.util.UUID;
public class HomeModule extends ModuleBase {
    

    public HomeModule(ModuleProperties properties) {
        super(properties);
    }

    @Override
    public void init() {
        registerConfig(HomeConfig.class, HomeConfig::new);
        registerPlayerData(HomePlayerData.class, HomePlayerData::new);
        registerLocale(HomeLocale.MODULE);

        commands.add(new HomeCommand(this));
        commands.add(new SetHomeCommand(this));
        commands.add(new HomesCommand(this));
        commands.add(new DeleteHomeCommand(this));
        commands.add(new HomeOtherCommand(this));
    }

    public HomePlayerData getData(UUID player) {
        return Solstice.playerData.get(player).getData(HomePlayerData.class);
    }

    public HomeConfig getConfig() {
        return Solstice.configManager.getData(HomeConfig.class);
    }
}
