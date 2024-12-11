package me.alexdevs.solstice.modules.seen;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.seen.commands.SeenCommand;
import me.alexdevs.solstice.modules.seen.data.SeenLocale;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class SeenModule {
    public static final String ID = "seen";
    public SeenModule() {
        Solstice.localeManager.registerModule(ID, SeenLocale.MODULE);

        CommandRegistrationCallback.EVENT.register(SeenCommand::new);
    }
}
