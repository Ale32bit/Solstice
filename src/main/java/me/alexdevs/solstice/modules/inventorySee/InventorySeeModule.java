package me.alexdevs.solstice.modules.inventorySee;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.inventorySee.commands.InventorySeeCommand;
import me.alexdevs.solstice.modules.inventorySee.data.InventorySeeLocale;

public class InventorySeeModule extends ModuleBase.Toggleable {
    public static final String ID = "inventorysee";

    public InventorySeeModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.localeManager.registerModule(ID, InventorySeeLocale.MODULE);

        commands.add(new InventorySeeCommand(this));
    }
}
