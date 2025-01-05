package me.alexdevs.solstice.modules.kit;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class KitInventory implements Inventory {
    public static final int SIZE = 27;

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(SIZE, ItemStack.EMPTY);

    @Override
    public int size() {
        return SIZE;
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        var stack = getStack(slot);
        var taken = stack.copyWithCount(amount);
        stack.decrement(amount);
        return taken;
    }

    @Override
    public ItemStack removeStack(int slot) {
        var stack = items.get(slot);
        return stack.copyAndEmpty();
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        items.set(slot, stack);
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    @Override
    public void clear() {
        items.clear();
    }
}
