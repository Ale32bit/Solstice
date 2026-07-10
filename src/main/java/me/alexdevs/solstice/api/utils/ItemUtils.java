package me.alexdevs.solstice.api.utils;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.ResolvableProfile;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public class ItemUtils {
    public static void setCustomName(ItemStack stack, Component name) {
        stack.set(DataComponents.CUSTOM_NAME, name);
    }
    public static void removeCustomName(ItemStack stack) {
        stack.remove(DataComponents.CUSTOM_NAME);
    }
    public static void setLore(ItemStack stack, List<Component> lines) {
        stack.set(DataComponents.LORE, new ItemLore(lines));
    }
    public static void removeLore(ItemStack stack) {
        stack.remove(DataComponents.LORE);
    }
    public static void removeDamage(ItemStack stack) {
        stack.set(DataComponents.DAMAGE, 0);
    }
    public static void setGlint(ItemStack stack, boolean value) {
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, value);
    }
    public static void setProfileByName(ItemStack stack, String name) {
        //? >= 1.21.11
        //var profile = ResolvableProfile.createUnresolved(name);
        //? < 1.21.11 && >= 1.21.1
        var profile = new ResolvableProfile(Optional.of(name), Optional.empty(), new PropertyMap());

        stack.set(DataComponents.PROFILE, profile);
    }
}
