package me.alexdevs.solstice.modules.rtp;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.rtp.commands.RTPCommand;
import me.alexdevs.solstice.modules.rtp.data.RTPConfig;

public class RTPModule extends ModuleBase {
    public static final String ID = "rtp";
    public RTPModule() {
        super(ID);

        Solstice.configManager.registerData(ID, RTPConfig.class, RTPConfig::new);

        commands.add(new RTPCommand(this));
    }

    public RTPConfig getConfig() {
        return Solstice.configManager.getData(RTPConfig.class);
    }
}
