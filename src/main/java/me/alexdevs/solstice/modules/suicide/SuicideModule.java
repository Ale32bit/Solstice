package me.alexdevs.solstice.modules.suicide;

import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.suicide.commands.SuicideCommand;

import java.util.Collection;
import java.util.List;

public class SuicideModule extends ModuleBase {
    public static final String ID = "suicide";

    private final List<ModCommand<SuicideModule>> commands = List.of(
            new SuicideCommand(this)
    );

    public SuicideModule() {
        super(ID);
    }

    @Override
    public Collection<? extends ModCommand<?>> getCommands() {
        return commands;
    }
}
