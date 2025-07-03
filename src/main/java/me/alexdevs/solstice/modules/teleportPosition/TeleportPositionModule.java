package me.alexdevs.solstice.modules.teleportPosition;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.teleportPosition.commands.TeleportPositionCommand;
import net.minecraft.resources.ResourceLocation;

public class TeleportPositionModule extends ModuleBase.Toggleable {
    
    public TeleportPositionModule(ResourceLocation id) {
        super(id);
    }

    @Override
    public void init() {
        commands.add(new TeleportPositionCommand(this));
    }
}
