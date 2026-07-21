package me.alexdevs.solstice.modules.helpOp;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleProperties;
import me.alexdevs.solstice.modules.helpOp.commands.HelpOpCommand;
import me.alexdevs.solstice.modules.helpOp.data.HelpOpLocale;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;

public class HelpOpModule extends ModuleBase {
    public HelpOpModule(ModuleProperties properties) {
        super(properties);
    }

    @Override
    public void init() {
        registerLocale(HelpOpLocale.MODULE);

        commands.add(new HelpOpCommand(this));
    }
}
