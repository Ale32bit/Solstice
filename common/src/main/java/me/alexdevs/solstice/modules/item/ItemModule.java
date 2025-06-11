package me.alexdevs.solstice.modules.item;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.item.commands.ItemLoreCommand;
import me.alexdevs.solstice.modules.item.commands.ItemNameCommand;
import me.alexdevs.solstice.modules.item.commands.MoreCommand;
import me.alexdevs.solstice.modules.item.commands.RepairCommand;
import me.alexdevs.solstice.modules.item.data.ItemLocale;

public class ItemModule extends ModuleBase.Toggleable {
    public static final String ID = "item";
    public ItemModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.localeManager.registerModule(ID, ItemLocale.MODULE);

        commands.add(new ItemLoreCommand(this));
        commands.add(new ItemNameCommand(this));
        commands.add(new RepairCommand(this));
        commands.add(new MoreCommand(this));
    }
}
