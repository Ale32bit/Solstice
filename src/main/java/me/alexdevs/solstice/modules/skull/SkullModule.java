package me.alexdevs.solstice.modules.skull;

import com.mojang.authlib.GameProfile;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.skull.commands.SkullCommand;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlayerHeadItem;

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
        skull.addTagElement(PlayerHeadItem.TAG_SKULL_OWNER, StringTag.valueOf(name));
        return skull;
    }

    public ItemStack createSkull(UUID uuid) {
        return createSkull(uuid.toString()); // :shrug:
    }

    public ItemStack createSkull(GameProfile profile) {
        return createSkull(profile.getName());
    }
}
