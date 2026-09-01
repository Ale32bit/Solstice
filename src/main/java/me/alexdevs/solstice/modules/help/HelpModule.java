package me.alexdevs.solstice.modules.help;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
import me.alexdevs.solstice.modules.help.commands.HelpCommand;
import me.alexdevs.solstice.modules.help.data.HelpConfig;
import me.alexdevs.solstice.modules.help.data.HelpLocale;

public class HelpModule extends ModuleBase.Toggleable {
    public HelpModule(SolsticeIdentifier id) {
        super(id);
    }

    @Override
    public void init() {
        registerConfig(HelpConfig.class, HelpConfig::new);
        registerLocale(HelpLocale.MODULE);

        commands.add(new HelpCommand(this));
    }

    public HelpConfig getConfig() {
        return Solstice.configManager.getData(HelpConfig.class);
    }
}
