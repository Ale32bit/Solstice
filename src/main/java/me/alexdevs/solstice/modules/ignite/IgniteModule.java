package me.alexdevs.solstice.modules.ignite;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.ignite.commands.IgniteCommand;
import net.minecraft.resources.ResourceLocation;
public class IgniteModule extends ModuleBase.Toggleable {
    

    public IgniteModule(ResourceLocation id) {
        super(id);
    }

    @Override
    public void init() {
        commands.add(new IgniteCommand(this));
    }
}
