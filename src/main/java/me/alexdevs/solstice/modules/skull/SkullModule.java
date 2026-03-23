package me.alexdevs.solstice.modules.skull;

import com.mojang.authlib.GameProfile;
//? if >= 1.21.1 {
/*import com.mojang.authlib.properties.PropertyMap;
*///? }
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.skull.commands.SkullCommand;
//? if >= 1.21.1 {
/*import net.minecraft.core.component.DataComponents;
*///? }
//? if < 1.21.1 {
import net.minecraft.nbt.StringTag;
//? }
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
//? if < 1.21.1 {
import net.minecraft.world.item.PlayerHeadItem;
//? }
//? if >= 1.21.1 {
/*import net.minecraft.world.item.component.ResolvableProfile;
*///? }

//? if >= 1.21.1 {
/*import java.util.Optional;
*///? }
import java.util.UUID;

public class SkullModule extends ModuleBase.Toggleable {


    public SkullModule(ResourceLocation id) {
        super(id);
    }

    @Override
    public void init() {
        commands.add(new SkullCommand(this));
    }

    public ItemStack createSkull(String name) {
        var skull = Items.PLAYER_HEAD.getDefaultInstance();
        name = name.substring(0, Math.min(name.length(), 16));
        //? if >= 1.21.1 {
        /*skull.set(DataComponents.PROFILE, new ResolvableProfile(Optional.of(name), Optional.empty(), new PropertyMap()));
        *///? } else {
        skull.addTagElement(PlayerHeadItem.TAG_SKULL_OWNER, StringTag.valueOf(name));
        //? }
        return skull;
    }

    public ItemStack createSkull(UUID uuid) {
        //? if >= 1.21.1 {
        /*var skull = Items.PLAYER_HEAD.getDefaultInstance();
        skull.set(DataComponents.PROFILE, new ResolvableProfile(Optional.empty(), Optional.of(uuid), new PropertyMap()));
        return skull;
        *///? } else {
        return createSkull(uuid.toString()); // :shrug:
        //? }
    }

    public ItemStack createSkull(GameProfile profile) {
        //? if >= 1.21.1 {
        /*var skull = Items.PLAYER_HEAD.getDefaultInstance();
        skull.set(DataComponents.PROFILE, new ResolvableProfile(profile));
        return skull;
        *///? } else {
        return createSkull(profile.getName());
        //? }
    }
}
