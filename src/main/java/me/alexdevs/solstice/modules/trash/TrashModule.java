package me.alexdevs.solstice.modules.trash;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.trash.commands.TrashCommand;
import me.alexdevs.solstice.modules.trash.data.TrashLocale;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;

public class TrashModule extends ModuleBase.Toggleable {
    

    public TrashModule(SolsticeIdentifier id) {
        super(id);
    }

    @Override
    public void init() {
        registerLocale(TrashLocale.MODULE);

        commands.add(new TrashCommand(this));
    }
}
