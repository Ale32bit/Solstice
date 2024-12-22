package me.alexdevs.solstice.modules.hat;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.hat.commands.HatCommand;
import me.alexdevs.solstice.modules.hat.data.HatLocale;

public class HatModule extends ModuleBase {
    public static final String ID = "hat";

    public HatModule() {
        super(ID);

        Solstice.localeManager.registerModule(ID, HatLocale.MODULE);

        commands.add(new HatCommand(this));
    }
}
