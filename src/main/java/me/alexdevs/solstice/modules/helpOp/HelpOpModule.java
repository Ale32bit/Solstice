package me.alexdevs.solstice.modules.helpOp;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.helpOp.commands.HelpOpCommand;
import me.alexdevs.solstice.modules.helpOp.data.HelpOpLocale;

public class HelpOpModule extends ModuleBase {
    public static final String ID = "helpop";

    public HelpOpModule() {
        super(ID);

        Solstice.localeManager.registerModule(ID, HelpOpLocale.MODULE);

        commands.add(new HelpOpCommand(this));
    }
}
