package me.alexdevs.solstice.modules.trash;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.trash.commands.TrashCommand;
import me.alexdevs.solstice.modules.trash.data.TrashLocale;

public class TrashModule extends ModuleBase.Toggleable {
    public static final String ID = "trash";

    public TrashModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.localeManager.registerModule(ID, TrashLocale.MODULE);

        commands.add(new TrashCommand(this));
    }
}
