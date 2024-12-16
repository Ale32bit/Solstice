package me.alexdevs.solstice.modules.kit.data;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.Solstice;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtHelper;

import java.util.ArrayList;
import java.util.List;

public class Kit {
    /**
     * itemStacks nbt is serialized and deserialized
     */
    public List<String> itemStacks = new ArrayList<>();
    public boolean oneTime = false;
    public int cooldownSeconds = 0;
    public boolean firstJoin = false;

    public List<ItemStack> getItemStacks() {
        var stacks = new ArrayList<ItemStack>();

        for(var stackNbt : itemStacks) {
            try {
                stacks.add(ItemStack.fromNbt(NbtHelper.fromNbtProviderString(stackNbt)));
            } catch (CommandSyntaxException e) {
                Solstice.LOGGER.error("Could not load item from kit", e);
            }
        }

        return stacks;
    }
}
