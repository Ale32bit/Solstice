package me.alexdevs.solstice.modules.admin;

import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.admin.commands.*;
import me.alexdevs.solstice.modules.sudo.commands.DoAsCommand;
import me.alexdevs.solstice.modules.sudo.commands.SudoCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import java.util.Collection;
import java.util.List;

public class AdminModule extends ModuleBase {
    public static final String ID = "admin";
    public AdminModule() {
        super(ID);
    }

    @Override
    public Collection<? extends ModCommand<?>> getCommands() {
        return List.of();
    }
}
