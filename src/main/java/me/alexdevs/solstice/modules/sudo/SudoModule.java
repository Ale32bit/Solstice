package me.alexdevs.solstice.modules.sudo;

import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.sudo.commands.DoAsCommand;
import me.alexdevs.solstice.modules.sudo.commands.SudoCommand;

import java.util.Collection;
import java.util.List;

public class SudoModule extends ModuleBase {
    public static final String ID = "sudo";

    private final List<ModCommand<SudoModule>> commands = List.of(
            new SudoCommand(this),
            new DoAsCommand(this)
    );

    public SudoModule() {
        super(ID);
    }

    @Override
    public Collection<? extends ModCommand<?>> getCommands() {
        return commands;
    }
}
