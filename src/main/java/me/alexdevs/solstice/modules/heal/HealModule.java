package me.alexdevs.solstice.modules.heal;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.heal.commands.HealCommand;
import me.alexdevs.solstice.modules.heal.data.HealLocale;

public class HealModule extends ModuleBase.Toggleable {
    public static final String ID = "heal";

    public HealModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.localeManager.registerModule(ID, HealLocale.MODULE);

        commands.add(new HealCommand(this));
    }
}
