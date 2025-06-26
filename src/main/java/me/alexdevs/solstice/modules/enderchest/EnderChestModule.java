package me.alexdevs.solstice.modules.enderchest;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.enderchest.commands.EnderChestCommand;
import me.alexdevs.solstice.modules.enderchest.data.EnderChestLocale;
import net.minecraft.resources.ResourceLocation;
public class EnderChestModule extends ModuleBase.Toggleable {
    

    public EnderChestModule(ResourceLocation id) {
        super(id);
    }

    @Override
    public void init() {
        registerLocale(EnderChestLocale.LOCALE);

        commands.add(new EnderChestCommand(this));
    }
}
