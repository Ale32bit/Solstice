package me.alexdevs.solstice.modules.skull;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.skull.commands.SkullCommand;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.Optional;
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
        skull.set(DataComponents.PROFILE, new ResolvableProfile(Optional.of(name), Optional.empty(), new PropertyMap()));
        return skull;
    }

    public ItemStack createSkull(UUID uuid) {
        var skull = Items.PLAYER_HEAD.getDefaultInstance();
        skull.set(DataComponents.PROFILE, new ResolvableProfile(Optional.empty(), Optional.of(uuid), new PropertyMap()));
        return skull;
    }

    public ItemStack createSkull(GameProfile profile) {
        var skull = Items.PLAYER_HEAD.getDefaultInstance();
        skull.set(DataComponents.PROFILE, new ResolvableProfile(profile));
        return skull;
    }
}
