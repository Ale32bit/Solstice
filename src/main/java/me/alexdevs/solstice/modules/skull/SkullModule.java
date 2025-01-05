package me.alexdevs.solstice.modules.skull;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.skull.commands.SkullCommand;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SkullItem;
import net.minecraft.nbt.NbtString;

public class SkullModule extends ModuleBase {
    public static final String ID = "skull";

    public SkullModule() {
        super(ID);

        commands.add(new SkullCommand(this));
    }

    public ItemStack createSkull(String skullOwner) {
        var skull = Items.PLAYER_HEAD.getDefaultStack();
        skull.setSubNbt(SkullItem.SKULL_OWNER_KEY, NbtString.of(skullOwner));
        return skull;
    }
}
