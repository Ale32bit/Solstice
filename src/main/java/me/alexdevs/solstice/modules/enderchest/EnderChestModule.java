package me.alexdevs.solstice.modules.enderchest;

import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.enderchest.commands.EnderChestCommand;

import java.util.Collection;
import java.util.List;

public class EnderChestModule extends ModuleBase {
    public static final String ID = "enderchest";

    private final List<ModCommand<EnderChestModule>> commands = List.of(
            new EnderChestCommand(this)
    );

    public EnderChestModule() {
        super(ID);
    }

    @Override
    public Collection<? extends ModCommand<?>> getCommands() {
        return commands;
    }
}
