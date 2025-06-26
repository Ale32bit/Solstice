package me.alexdevs.solstice.modules.experiments;

import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.experiments.commands.FlagsCommand;
import me.alexdevs.solstice.modules.experiments.commands.TimeSpanCommand;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.List;
public class ExperimentsModule extends ModuleBase {
    public static final boolean ENABLED = false;
    

    public ExperimentsModule(ResourceLocation id) {
        super(id);
    }

    @Override
    public void init() {
        commands.add(new TimeSpanCommand(this));
        commands.add(new FlagsCommand(this));
    }

    @Override
    public Collection<? extends ModCommand<?>> getCommands() {
        if (!ENABLED) return List.of();

        return commands;
    }
}
