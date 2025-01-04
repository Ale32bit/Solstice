package me.alexdevs.solstice.modules.kit;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static List<ItemStack> getItems(SimpleGui container) {
        var items = new ArrayList<ItemStack>();

        for (var i = 0; i < container.getSize(); i++) {
            var slot = container.getSlot(i);
            if (slot == null)
                continue;

            var itemStack = slot.getItemStack();
            items.add(itemStack);
        }

        return items;
    }

    public static String serializeItemStack(ItemStack itemStack) {
        var nbt = new NbtCompound();
        itemStack.writeNbt(nbt);
        return nbt.asString();
    }

    public static ItemStack deserializeItemStack(String string) throws CommandSyntaxException {
        var nbt = StringNbtReader.parse(string);
        return ItemStack.fromNbt(nbt);
    }

    public static KitInventory createInventory(List<ItemStack> items) {
        var inventory = new KitInventory();
        for (var i = 0; i < items.size(); i++) {
            inventory.setStack(i, items.get(i));
        }
        return inventory;
    }

    public static List<ItemStack> getItemStacks(KitInventory inventory) {
        var items = new ArrayList<ItemStack>();
        for (var i = 0; i < inventory.size(); i++) {
            var stack = inventory.getStack(i);
            if(!stack.isEmpty()) {
                items.add(stack);
            }
        }
        return items;
    }

    public static void redirect(SimpleGui container, KitInventory inventory) {
        for(var i = 0; i < container.getSize(); i++) {
            container.setSlotRedirect(i, new Slot(inventory, i, 0, 0));
        }
    }
}
