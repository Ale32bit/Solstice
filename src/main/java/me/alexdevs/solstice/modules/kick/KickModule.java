package me.alexdevs.solstice.modules.kick;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.kick.commands.KickCommand;

public class KickModule extends ModuleBase {
    public static final String ID = "kick";

    public KickModule() {
        super(ID);

        commands.add(new KickCommand(this));
    }
}
