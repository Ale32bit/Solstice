package me.alexdevs.solstice.modules.sudo;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.sudo.commands.DoAsCommand;
import me.alexdevs.solstice.modules.sudo.commands.SudoCommand;

public class SudoModule extends ModuleBase.Toggleable {
    public static final String ID = "sudo";

    public SudoModule() {
        super(ID);
    }

    @Override
    public void init() {
        commands.add(new SudoCommand(this));
        commands.add(new DoAsCommand(this));
    }
}
