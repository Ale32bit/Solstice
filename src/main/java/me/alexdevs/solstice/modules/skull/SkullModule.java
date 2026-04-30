package me.alexdevs.solstice.modules.skull;

import com.mojang.authlib.GameProfile;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.utils.ItemUtils;
import me.alexdevs.solstice.modules.skull.commands.SkullCommand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
        ItemUtils.setProfileByName(skull, name);
        return skull;
    }
    public ItemStack createSkull(UUID uuid) {
        var skull = Items.PLAYER_HEAD.getDefaultInstance();
        ItemUtils.setProfileByUUID(skull, uuid);
        return skull;
    }

    public ItemStack createSkull(GameProfile profile) {
        var skull = Items.PLAYER_HEAD.getDefaultInstance();
        ItemUtils.setProfile(skull, profile);
        return skull;
    }
}
