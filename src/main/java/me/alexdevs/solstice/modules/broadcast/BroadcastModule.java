package me.alexdevs.solstice.modules.broadcast;

import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.broadcast.commands.BroadcastCommand;

import java.util.Collection;
import java.util.List;

public class BroadcastModule extends ModuleBase {
    private final Collection<? extends ModCommand<BroadcastModule>> commands;

    public BroadcastModule() {
        super("broadcast");

        commands = List.of(new BroadcastCommand(this));
    }

    @Override
    public Collection<? extends ModCommand<?>> getCommands() {
        return commands;
    }
}
