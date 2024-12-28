package me.alexdevs.solstice.modules.home;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.home.commands.*;
import me.alexdevs.solstice.modules.home.data.HomeConfig;
import me.alexdevs.solstice.modules.home.data.HomeLocale;
import me.alexdevs.solstice.modules.home.data.HomePlayerData;

import java.util.UUID;

public class HomeModule extends ModuleBase {
    public static final String ID = "home";

    public HomeModule() {
        super(ID);

        Solstice.configManager.registerData(ID, HomeConfig.class, HomeConfig::new);
        Solstice.playerData.registerData(ID, HomePlayerData.class, HomePlayerData::new);
        Solstice.localeManager.registerModule(ID, HomeLocale.MODULE);

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
