package me.alexdevs.solstice.modules.kit;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.sgui.api.gui.SimpleGui;
//? if < 1.21.1 {
/*import net.minecraft.nbt.CompoundTag;
*///? }
//? if >= 1.21.1 {
import me.alexdevs.solstice.Solstice;
//? }
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Utils {




    public static String serializeItemStack(ItemStack itemStack) {
        //? if >= 1.21.1 {
        var registry = Solstice.server.registryAccess();
        var registryOps = RegistryOps.create(NbtOps.INSTANCE, registry);
        return ItemStack.CODEC.encodeStart(registryOps, itemStack).getOrThrow().toString();
        //? } elif >= 1.21.1 {
        /*var registry = Solstice.server.registryAccess();
        return itemStack.save(registry).getAsString();
        *///? } else {
        /*var nbt = new CompoundTag();
        return itemStack.save(nbt).getAsString();
        *///? }
    }

    public static ItemStack deserializeItemStack(String string) throws CommandSyntaxException {
        //? if >= 1.21.11 {
        /*var registry = Solstice.server.registryAccess();
        var registryOps = RegistryOps.create(NbtOps.INSTANCE, registry);
        var tag = TagParser.parseCompoundFully(string);
        return ItemStack.OPTIONAL_CODEC.parse(registryOps, tag).getOrThrow();
        *///? } elif >= 1.21.1 {
        var registry = Solstice.server.registryAccess();
        return ItemStack.parseOptional(registry, TagParser.parseTag(string));
        //? } else {
        /*var nbt = TagParser.parseTag(string);
        return ItemStack.of(nbt);
        *///? }
    }

    public static KitInventory createInventory(List<ItemStack> items) {
        var inventory = new KitInventory();
        for (var i = 0; i < items.size(); i++) {
            inventory.setItem(i, items.get(i));
        }
        return inventory;
    }

    public static List<ItemStack> getItemStacks(KitInventory inventory) {
        var items = new ArrayList<ItemStack>();
        for (var i = 0; i < inventory.getContainerSize(); i++) {
            var stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                items.add(stack);
            }
        }
        return items;
    }

    public static void redirect(SimpleGui container, KitInventory inventory) {
        for (var i = 0; i < container.getSize(); i++) {
            container.setSlotRedirect(i, new Slot(inventory, i, 0, 0));
        }
    }
}
