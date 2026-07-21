package me.alexdevs.solstice.modules.heal;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleProperties;
import me.alexdevs.solstice.modules.heal.commands.HealCommand;
import me.alexdevs.solstice.modules.heal.data.HealLocale;

public class HealModule extends ModuleBase {
    public HealModule(ModuleProperties properties) {
        super(properties);
    }

    @Override
    public void init() {
        registerLocale(HealLocale.MODULE);

        commands.add(new HealCommand(this));
    }
}
