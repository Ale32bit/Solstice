package me.alexdevs.solstice.modules.hat;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.hat.commands.HatCommand;
import me.alexdevs.solstice.modules.hat.data.HatLocale;

import java.util.Collection;
import java.util.List;

public class HatModule extends ModuleBase {
    public static final String ID = "hat";

    private final List<ModCommand<HatModule>> commands = List.of(
            new HatCommand(this)
    );

    public HatModule() {
        super(ID);

        Solstice.localeManager.registerModule(ID, HatLocale.MODULE);
    }

    @Override
    public Collection<? extends ModCommand<?>> getCommands() {
        return commands;
    }
}
