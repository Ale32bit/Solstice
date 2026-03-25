package me.alexdevs.solstice.modules.skull;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.utils.ItemUtils;
import me.alexdevs.solstice.modules.skull.commands.SkullCommand;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SkullModule extends ModuleBase.Toggleable {


    public SkullModule(SolsticeIdentifier id) {
        super(id);
    }

    @Override
    public void init() {
        commands.add(new SkullCommand(this));
    }

    public ItemStack createSkull(String name) {
        var skull = Items.PLAYER_HEAD.getDefaultInstance();
        name = name.substring(0, Math.min(name.length(), 16));
        ItemUtils.setProfileByName(skull, name);
        return skull;
    }
}
