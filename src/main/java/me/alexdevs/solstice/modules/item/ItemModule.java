package me.alexdevs.solstice.modules.item;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.item.commands.ItemLoreCommand;
import me.alexdevs.solstice.modules.item.commands.ItemNameCommand;
import me.alexdevs.solstice.modules.item.commands.MoreCommand;
import me.alexdevs.solstice.modules.item.commands.RepairCommand;
import me.alexdevs.solstice.modules.item.data.ItemLocale;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
public class ItemModule extends ModuleBase.Toggleable {
    
    public ItemModule(SolsticeIdentifier id) {
        super(id);
    }

    @Override
    public void init() {
        registerLocale(ItemLocale.MODULE);

        commands.add(new ItemLoreCommand(this));
        commands.add(new ItemNameCommand(this));
        commands.add(new RepairCommand(this));
        commands.add(new MoreCommand(this));
    }
}
