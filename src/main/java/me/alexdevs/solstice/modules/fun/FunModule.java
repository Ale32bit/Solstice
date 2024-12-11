package me.alexdevs.solstice.modules.fun;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.fun.commands.HatCommand;
import me.alexdevs.solstice.modules.fun.data.FunLocale;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class FunModule {
    public static final String ID = "fun";

    public FunModule() {
        Solstice.localeManager.registerModule(ID, FunLocale.MODULE);
        CommandRegistrationCallback.EVENT.register(HatCommand::new);
    }
}
