package me.alexdevs.solstice.modules.sudo;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.sudo.commands.DoAsCommand;
import me.alexdevs.solstice.modules.sudo.commands.SudoCommand;
import net.minecraft.resources.ResourceLocation;

public class SudoModule extends ModuleBase.Toggleable {
    public SudoModule(ResourceLocation id) {
        super(id);
    }

    @Override
    public void init() {
        commands.add(new SudoCommand(this));
        commands.add(new DoAsCommand(this));
    }
}
