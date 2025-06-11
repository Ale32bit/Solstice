package me.alexdevs.solstice.modules.kit.data;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.kit.Utils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

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
    public @Nullable String icon;

    public List<ItemStack> getItemStacks() {
        var stacks = new ArrayList<ItemStack>();

        for (var stackNbt : itemStacks) {
            try {
                stacks.add(Utils.deserializeItemStack(stackNbt));
            } catch (CommandSyntaxException e) {
                Solstice.LOGGER.error("Could not load item from kit", e);
            }
        }

        return stacks;
    }

    public ItemStack getIcon() {
        var defaultStack = Items.DIRT.getDefaultInstance();
        if (icon == null) {
            return defaultStack;
        }
        try {
            return Utils.deserializeItemStack(icon);
        } catch (CommandSyntaxException e) {
            return defaultStack;
        }
    }
}
