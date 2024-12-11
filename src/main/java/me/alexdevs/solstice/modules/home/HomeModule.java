package me.alexdevs.solstice.modules.home;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.locale.Locale;
import me.alexdevs.solstice.modules.home.commands.*;
import me.alexdevs.solstice.modules.home.data.HomeConfig;
import me.alexdevs.solstice.modules.home.data.HomeLocale;
import me.alexdevs.solstice.modules.home.data.HomePlayerData;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import java.util.UUID;

public class HomeModule {
    public static final String ID = "home";

    public HomeModule() {
        Solstice.configManager.registerData(ID, HomeConfig.class, HomeConfig::new);
        Solstice.playerData.registerData(ID, HomePlayerData.class, HomePlayerData::new);
        Solstice.localeManager.registerModule(ID, HomeLocale.MODULE);

        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            new DeleteHomeCommand(commandDispatcher, commandRegistryAccess, registrationEnvironment);
            new HomeCommand(commandDispatcher, commandRegistryAccess, registrationEnvironment);
            new HomeOther(commandDispatcher, commandRegistryAccess, registrationEnvironment);
            new HomesCommand(commandDispatcher, commandRegistryAccess, registrationEnvironment);
            new SetHomeCommand(commandDispatcher, commandRegistryAccess, registrationEnvironment);
        });
    }

    public HomePlayerData getData(UUID player) {
        return Solstice.playerData.get(player).getData(HomePlayerData.class);
    }

    public Locale getLocale() {
        return Solstice.localeManager.getLocale(ID);
    }

    public HomeConfig getConfig() {
        return Solstice.configManager.getData(HomeConfig.class);
    }
}
