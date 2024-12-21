package me.alexdevs.solstice.modules.seen;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.seen.commands.SeenCommand;
import me.alexdevs.solstice.modules.seen.data.SeenLocale;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import java.util.Collection;
import java.util.List;

public class SeenModule extends ModuleBase {
    public static final String ID = "seen";
    public SeenModule() {
        super(ID);

        Solstice.localeManager.registerModule(ID, SeenLocale.MODULE);

        CommandRegistrationCallback.EVENT.register(SeenCommand::new);
    }

    @Override
    public Collection<? extends ModCommand<?>> getCommands() {
        return List.of();
    }
}
