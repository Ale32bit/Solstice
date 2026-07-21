package me.alexdevs.solstice.modules.enderchest;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleProperties;
import me.alexdevs.solstice.modules.enderchest.commands.EnderChestCommand;
import me.alexdevs.solstice.modules.enderchest.data.EnderChestLocale;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
public class EnderChestModule extends ModuleBase {
    

    public EnderChestModule(ModuleProperties properties) {
        super(properties);
    }

    @Override
    public void init() {
        registerLocale(EnderChestLocale.LOCALE);

        commands.add(new EnderChestCommand(this));
    }
}
