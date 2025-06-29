package me.alexdevs.solstice.modules.helpOp;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.helpOp.commands.HelpOpCommand;
import me.alexdevs.solstice.modules.helpOp.data.HelpOpLocale;
import net.minecraft.resources.ResourceLocation;

public class HelpOpModule extends ModuleBase.Toggleable {
    public HelpOpModule(ResourceLocation id) {
        super(id);
    }

    @Override
    public void init() {
        registerLocale(HelpOpLocale.MODULE);

        commands.add(new HelpOpCommand(this));
    }
}
