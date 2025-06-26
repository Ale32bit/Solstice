package me.alexdevs.solstice.modules.heal;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.heal.commands.HealCommand;
import net.minecraft.resources.ResourceLocation;
import me.alexdevs.solstice.modules.heal.data.HealLocale;

public class HealModule extends ModuleBase.Toggleable {
    public HealModule(ResourceLocation id) {
        super(id);
    }

    @Override
    public void init() {
        registerLocale(HealLocale.MODULE);

        commands.add(new HealCommand(this));
    }
}
