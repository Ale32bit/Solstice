package me.alexdevs.solstice.modules.near;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.near.commands.NearCommand;
import me.alexdevs.solstice.modules.near.data.NearConfig;
import me.alexdevs.solstice.modules.near.data.NearLocale;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class NearModule {
    public static final String ID = "near";
    public NearModule() {
        Solstice.configManager.registerData(ID, NearConfig.class, NearConfig::new);
        Solstice.localeManager.registerModule(ID, NearLocale.MODULE);

        CommandRegistrationCallback.EVENT.register(NearCommand::new);
    }
}
