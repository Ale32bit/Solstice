package me.alexdevs.solstice.modules.skull;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.skull.commands.SkullCommand;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.Optional;
import java.util.UUID;

public class SkullModule extends ModuleBase.Toggleable {
    public static final String ID = "skull";

    public SkullModule() {
        super(ID);
    }

    @Override
    public void init() {
        commands.add(new SkullCommand(this));
    }

    public ItemStack createSkull(String name) {
        var skull = Items.PLAYER_HEAD.getDefaultStack();
        name = name.substring(0, Math.min(name.length(), 16));
        skull.set(DataComponentTypes.PROFILE, new ProfileComponent(Optional.of(name), Optional.empty(), new PropertyMap()));
        return skull;
    }

    public ItemStack createSkull(UUID uuid) {
        var skull = Items.PLAYER_HEAD.getDefaultStack();
        skull.set(DataComponentTypes.PROFILE, new ProfileComponent(Optional.empty(), Optional.of(uuid), new PropertyMap()));
        return skull;
    }

    public ItemStack createSkull(GameProfile profile) {
        var skull = Items.PLAYER_HEAD.getDefaultStack();
        skull.set(DataComponentTypes.PROFILE, new ProfileComponent(profile));
        return skull;
    }
}
