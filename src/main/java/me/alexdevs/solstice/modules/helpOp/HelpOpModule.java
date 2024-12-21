package me.alexdevs.solstice.modules.helpOp;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.helpOp.commands.HelpOpCommand;
import me.alexdevs.solstice.modules.helpOp.data.HelpOpLocale;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import java.util.Collection;
import java.util.List;

public class HelpOpModule extends ModuleBase {
    public static final String ID = "helpop";
    public static final String HELPOP_RECEIVER_PERMISSION = "solstice.helpop.receiver";

    public HelpOpModule() {
        super(ID);

        Solstice.localeManager.registerModule(ID, HelpOpLocale.MODULE);

        CommandRegistrationCallback.EVENT.register(HelpOpCommand::new);
    }

    @Override
    public Collection<? extends ModCommand<?>> getCommands() {
        return List.of();
    }
}
