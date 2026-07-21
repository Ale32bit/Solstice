package me.alexdevs.solstice.modules.utilities;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleProperties;
import me.alexdevs.solstice.modules.utilities.commands.*;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;

public class UtilitiesModule extends ModuleBase {
    public UtilitiesModule(ModuleProperties properties) {
        super(properties);
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
