package me.alexdevs.solstice.modules.broadcast;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.broadcast.commands.BroadcastCommand;
import me.alexdevs.solstice.modules.broadcast.commands.PlainBroadcastCommand;
import me.alexdevs.solstice.modules.broadcast.data.BroadcastConfig;

public class BroadcastModule extends ModuleBase.Toggleable {
    public static final String ID = "broadcast";

    public BroadcastModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.configManager.registerData(ID, BroadcastConfig.class, BroadcastConfig::new);

        commands.add(new BroadcastCommand(this));
        commands.add(new PlainBroadcastCommand(this));
    }
}
