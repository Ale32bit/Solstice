package me.alexdevs.solstice.modules.near;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.near.commands.NearCommand;
import me.alexdevs.solstice.modules.near.data.NearConfig;
import me.alexdevs.solstice.modules.near.data.NearLocale;

public class NearModule extends ModuleBase.Toggleable {
    public static final String ID = "near";

    public NearModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.configManager.registerData(ID, NearConfig.class, NearConfig::new);
        Solstice.localeManager.registerModule(ID, NearLocale.MODULE);

        commands.add(new NearCommand(this));
    }
}
