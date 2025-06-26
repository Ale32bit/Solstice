package me.alexdevs.solstice.modules.broadcast;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.broadcast.commands.BroadcastCommand;
import me.alexdevs.solstice.modules.broadcast.commands.PlainBroadcastCommand;
import me.alexdevs.solstice.modules.broadcast.data.BroadcastConfig;
import net.minecraft.resources.ResourceLocation;
public class BroadcastModule extends ModuleBase.Toggleable {
    

    public BroadcastModule(ResourceLocation id) {
        super(id);
    }

    @Override
    public void init() {
        registerConfig(BroadcastConfig.class, BroadcastConfig::new);

        commands.add(new BroadcastCommand(this));
        commands.add(new PlainBroadcastCommand(this));
    }
}
