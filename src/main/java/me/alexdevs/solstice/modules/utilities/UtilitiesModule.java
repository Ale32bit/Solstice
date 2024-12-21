package me.alexdevs.solstice.modules.utilities;

import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.suicide.commands.SuicideCommand;
import me.alexdevs.solstice.modules.trash.TrashCommand;
import me.alexdevs.solstice.modules.utilities.commands.*;

import java.util.Collection;
import java.util.List;

public class UtilitiesModule extends ModuleBase {
    public static final String ID = "utilities";

    private final List<ModCommand<UtilitiesModule>> commands = List.of(
            new AnvilCommand(this),
            new CartographyCommand(this),
            new GrindstoneCommand(this),
            new LoomCommand(this),
            new SmithingCommand(this),
            new StonecutterCommand(this),
            new WorkbenchCommand(this)
    );

    public UtilitiesModule() {
        super(ID);
    }

    @Override
    public Collection<? extends ModCommand<?>> getCommands() {
        return commands;
    }
}
