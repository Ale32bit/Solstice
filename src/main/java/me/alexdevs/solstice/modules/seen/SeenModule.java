package me.alexdevs.solstice.modules.seen;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.seen.commands.SeenCommand;
import me.alexdevs.solstice.modules.seen.data.SeenLocale;

public class SeenModule extends ModuleBase {
    public static final String ID = "seen";

    public SeenModule() {
        super(ID);

        Solstice.localeManager.registerModule(ID, SeenLocale.MODULE);

        commands.add(new SeenCommand(this));
    }
}
