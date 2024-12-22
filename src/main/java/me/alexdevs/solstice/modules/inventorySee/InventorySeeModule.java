package me.alexdevs.solstice.modules.inventorySee;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.inventorySee.commands.InventorySeeCommand;

public class InventorySeeModule extends ModuleBase {
    public static final String ID = "inventorysee";

    public InventorySeeModule() {
        super(ID);

        commands.add(new InventorySeeCommand(this));
    }
}
