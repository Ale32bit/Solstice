package me.alexdevs.solstice.modules.broadcast;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleProperties;
import me.alexdevs.solstice.modules.broadcast.commands.BroadcastCommand;
import me.alexdevs.solstice.modules.broadcast.commands.PlainBroadcastCommand;
import me.alexdevs.solstice.modules.broadcast.data.BroadcastConfig;

public class BroadcastModule extends ModuleBase {


    public BroadcastModule(ModuleProperties properties) {
        super(properties);
    }

    @Override
    public void init() {
        registerConfig(BroadcastConfig.class, BroadcastConfig::new);

        commands.add(new BroadcastCommand(this));
        commands.add(new PlainBroadcastCommand(this));
    }
}
