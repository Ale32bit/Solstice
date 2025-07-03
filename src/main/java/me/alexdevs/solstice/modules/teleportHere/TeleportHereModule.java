package me.alexdevs.solstice.modules.teleportHere;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.teleportHere.commands.TeleportHereCommand;
import net.minecraft.resources.ResourceLocation;

public class TeleportHereModule extends ModuleBase.Toggleable {
    

    public TeleportHereModule(ResourceLocation id) {
        super(id);
    }

    @Override
    public void init() {
        commands.add(new TeleportHereCommand(this));
    }
}
