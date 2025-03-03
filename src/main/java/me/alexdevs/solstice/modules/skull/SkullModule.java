package me.alexdevs.solstice.modules.skull;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.skull.commands.SkullCommand;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlayerHeadItem;

public class SkullModule extends ModuleBase.Toggleable {
    public static final String ID = "skull";

    public SkullModule() {
        super(ID);
    }

    @Override
    public void init() {
        commands.add(new SkullCommand(this));
    }

    public ItemStack createSkull(String skullOwner) {
        var skull = Items.PLAYER_HEAD.getDefaultInstance();
        skull.addTagElement(PlayerHeadItem.TAG_SKULL_OWNER, StringTag.valueOf(skullOwner));
        return skull;
    }
}
