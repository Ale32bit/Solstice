package me.alexdevs.solstice.modules.sudo;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleProperties;
import me.alexdevs.solstice.modules.sudo.commands.DoAsCommand;
import me.alexdevs.solstice.modules.sudo.commands.SudoCommand;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;

public class SudoModule extends ModuleBase {
    public SudoModule(ModuleProperties properties) {
        super(properties);
    }

    @Override
    public void init() {
        commands.add(new SudoCommand(this));
        commands.add(new DoAsCommand(this));
    }
}
