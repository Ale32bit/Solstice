package me.alexdevs.solstice.modules.smite;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.smite.commands.SmiteCommand;
import net.minecraft.resources.ResourceLocation;
public class SmiteModule extends ModuleBase.Toggleable {
    

    public SmiteModule(ResourceLocation id) {
        super(id);
    }

    @Override
    public void init() {
        commands.add(new SmiteCommand(this));
    }
}
