package me.alexdevs.solstice.api.utils;
import com.mojang.authlib.GameProfile;
//? if >= 1.21.1 {
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.ResolvableProfile;
import java.util.Optional;
//? } else {
/*import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.PlayerHeadItem;
*///? }
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import java.util.UUID;
public class ItemUtils {
    public static void setCustomName(ItemStack stack, Component name) {
        //? >= 1.21.1
        stack.set(DataComponents.CUSTOM_NAME, name);
        //? < 1.21.1
        //stack.setHoverName(name);
    }
    public static void removeCustomName(ItemStack stack) {
        //? >= 1.21.1
        stack.remove(DataComponents.CUSTOM_NAME);
        //? < 1.21.1
        //stack.resetHoverName();
    }
    public static void setLore(ItemStack stack, List<Component> lines) {
        //? if >= 1.21.1 {
        stack.set(DataComponents.LORE, new ItemLore(lines));
        //? } else {
        /*var list = new ListTag();
        for (var line : lines) {
            list.add(StringTag.valueOf(Component.Serializer.toJson(line)));
        }
        var displayNbt = stack.getOrCreateTagElement("display");
        displayNbt.put("Lore", list);
        *///? }
    }
    public static void removeLore(ItemStack stack) {
        //? if >= 1.21.1 {
        stack.remove(DataComponents.LORE);
        //? } else {
        /*var nbtCompound = stack.getTagElement("display");
        if (nbtCompound != null) {
            nbtCompound.remove("Lore");
            if (nbtCompound.isEmpty()) {
                stack.removeTagKey("display");
            }
        }
        *///? }
    }
    public static void removeDamage(ItemStack stack) {
        //? >= 1.21.1
        stack.remove(DataComponents.DAMAGE);
        //? < 1.21.1
        //stack.removeTagKey("Damage");
    }
    public static void setGlint(ItemStack stack, boolean value) {
        //? if >= 1.21.1 {
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, value);
        //? } else {
        /*if (value) {
            stack.getOrCreateTag().put("Enchantments", new ListTag());
        }
        *///? }
    }
    public static void setProfileByName(ItemStack stack, String name) {
        //? >= 1.21.1
        stack.set(DataComponents.PROFILE,new ResolvableProfile(Optional.of(name), Optional.empty(), new PropertyMap()));
        //? < 1.21.1
        //stack.addTagElement(PlayerHeadItem.TAG_SKULL_OWNER, StringTag.valueOf(name));
    }
    public static void setProfileByUUID(ItemStack stack, UUID uuid) {
        //? >= 1.21.1
        stack.set(DataComponents.PROFILE,new ResolvableProfile(Optional.empty(), Optional.of(uuid), new PropertyMap()));
        //? < 1.21.1
        //stack.addTagElement(PlayerHeadItem.TAG_SKULL_OWNER, StringTag.valueOf(uuid.toString()));
    }
    public static void setProfile(ItemStack stack, GameProfile profile) {
        //? >= 1.21.1
        stack.set(DataComponents.PROFILE, new ResolvableProfile(profile));
        //? < 1.21.1
        //stack.addTagElement(PlayerHeadItem.TAG_SKULL_OWNER, StringTag.valueOf(profile.getName()));
    }
}
