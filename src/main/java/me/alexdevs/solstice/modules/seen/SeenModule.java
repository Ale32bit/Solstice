package me.alexdevs.solstice.modules.seen;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.seen.commands.SeenCommand;
import me.alexdevs.solstice.modules.seen.data.SeenLocale;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
public class SeenModule extends ModuleBase.Toggleable {
    

    public SeenModule(SolsticeIdentifier id) {
        super(id);
    }

    @Override
    public void init() {
        registerLocale(SeenLocale.MODULE);

        commands.add(new SeenCommand(this));
    }
}
