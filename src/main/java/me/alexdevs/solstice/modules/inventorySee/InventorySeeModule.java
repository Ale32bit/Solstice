package me.alexdevs.solstice.modules.inventorySee;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.inventorySee.commands.InventorySeeCommand;
import me.alexdevs.solstice.modules.inventorySee.data.InventorySeeLocale;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
public class InventorySeeModule extends ModuleBase.Toggleable {
    

    public InventorySeeModule(SolsticeIdentifier id) {
        super(id);
    }

    @Override
    public void init() {
        registerLocale(InventorySeeLocale.MODULE);

        commands.add(new InventorySeeCommand(this));
    }
}
