package me.alexdevs.solstice.modules.helpop;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.helpop.commands.HelpOpCommand;
import me.alexdevs.solstice.modules.helpop.data.HelpOpLocale;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class HelpOpModule {
    public static final String ID = "helpop";
    public static final String HELPOP_RECEIVER_PERMISSION = "solstice.helpop.receiver";

    public HelpOpModule() {
        Solstice.localeManager.registerModule(ID, HelpOpLocale.MODULE);

        CommandRegistrationCallback.EVENT.register(HelpOpCommand::new);
    }
}
