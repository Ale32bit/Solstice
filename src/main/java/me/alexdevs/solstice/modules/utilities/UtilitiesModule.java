package me.alexdevs.solstice.modules.utilities;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.utilities.commands.*;

public class UtilitiesModule extends ModuleBase.Toggleable {
    public static final String ID = "utilities";

    public UtilitiesModule() {
        super(ID);
    }

    @Override
    public void init() {
        commands.add(new AnvilCommand(this));
        commands.add(new CartographyCommand(this));
        commands.add(new GrindstoneCommand(this));
        commands.add(new LoomCommand(this));
        commands.add(new SmithingCommand(this));
        commands.add(new StonecutterCommand(this));
        commands.add(new WorkbenchCommand(this));
    }
}
