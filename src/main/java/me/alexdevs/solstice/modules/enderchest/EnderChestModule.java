package me.alexdevs.solstice.modules.enderchest;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.enderchest.commands.EnderChestCommand;

public class EnderChestModule extends ModuleBase {
    public static final String ID = "enderchest";

    public EnderChestModule() {
        super(ID);

        commands.add(new EnderChestCommand(this));
    }
}
