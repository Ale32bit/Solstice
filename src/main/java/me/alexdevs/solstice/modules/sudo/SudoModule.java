package me.alexdevs.solstice.modules.sudo;

import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.module.ModuleBase;

import java.util.Collection;
import java.util.List;

public class SudoModule extends ModuleBase {
    public SudoModule(String id) {
        super("sudo");
    }

    @Override
    public Collection<? extends ModCommand<?>> getCommands() {
        return List.of();
    }
}
