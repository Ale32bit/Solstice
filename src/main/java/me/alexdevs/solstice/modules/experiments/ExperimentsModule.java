package me.alexdevs.solstice.modules.experiments;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.experiments.commands.*;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
import me.alexdevs.solstice.modules.experiments.data.ExperimentsConfig;

import java.util.Collection;
import java.util.List;

public class ExperimentsModule extends ModuleBase {
    public ExperimentsModule(SolsticeIdentifier id) {
        super(id);
    }

    @Override
    public void init() {
        registerConfig(ExperimentsConfig.class, ExperimentsConfig::new);

        commands.add(new TimeSpanCommand(this));
        commands.add(new FlagsCommand(this));
        commands.add(new SafeTeleportCommand(this));
        commands.add(new ButtonCommand(this));
        commands.add(new KittyCannonCommand(this));
        commands.add(new RocketCommand(this));
        commands.add(new LengthCommand(this));
    }

    public ExperimentsConfig getConfig() {
        return Solstice.configManager.getData(ExperimentsConfig.class);
    }

    @Override
    public Collection<? extends ModCommand<?>> getCommands() {
        if (getConfig().enabled) {
            return commands;
        } else {
            return List.of();
        }
    }
}
