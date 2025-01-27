package me.alexdevs.solstice.modules.enderchest;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.enderchest.commands.EnderChestCommand;
import me.alexdevs.solstice.modules.enderchest.data.EnderChestLocale;

public class EnderChestModule extends ModuleBase.Toggleable {
    public static final String ID = "enderchest";

    public EnderChestModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.localeManager.registerModule(ID, EnderChestLocale.LOCALE);

        commands.add(new EnderChestCommand(this));
    }
}
