package me.alexdevs.solstice.modules.extinguish;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.extinguish.commands.ExtinguishCommand;
import net.minecraft.resources.ResourceLocation;
public class ExtinguishModule extends ModuleBase.Toggleable {
    

    public ExtinguishModule(ResourceLocation id) {
        super(id);
    }

    @Override
    public void init() {
        commands.add(new ExtinguishCommand(this));
    }
}
