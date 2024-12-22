package me.alexdevs.solstice.modules.trash;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.trash.commands.TrashCommand;

public class TrashModule extends ModuleBase {
    public static final String ID = "trash";

    public TrashModule() {
        super(ID);

        commands.add(new TrashCommand(this));
    }
}
