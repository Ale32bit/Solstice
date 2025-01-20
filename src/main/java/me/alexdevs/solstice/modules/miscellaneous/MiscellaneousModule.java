package me.alexdevs.solstice.modules.miscellaneous;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.miscellaneous.commands.EffectsCommand;
import me.alexdevs.solstice.modules.miscellaneous.data.MiscellaneousLocale;

public class MiscellaneousModule extends ModuleBase.Toggleable {
    public static final String ID = "miscellaneous";
    public MiscellaneousModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.localeManager.registerModule(ID, MiscellaneousLocale.MODULE);

        commands.add(new EffectsCommand(this));
    }
}
