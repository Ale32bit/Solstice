package me.alexdevs.solstice.modules.suicide;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.suicide.commands.SuicideCommand;
import net.minecraft.resources.ResourceLocation;

public class SuicideModule extends ModuleBase.Toggleable {
    

    public SuicideModule(ResourceLocation id) {
        super(id);
    }

    @Override
    public void init() {
        commands.add(new SuicideCommand(this));
    }
}
