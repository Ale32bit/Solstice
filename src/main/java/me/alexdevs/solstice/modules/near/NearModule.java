package me.alexdevs.solstice.modules.near;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.near.commands.NearCommand;
import me.alexdevs.solstice.modules.near.data.NearConfig;
import me.alexdevs.solstice.modules.near.data.NearLocale;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import java.util.Collection;
import java.util.List;

public class NearModule extends ModuleBase {
    public static final String ID = "near";
    public NearModule() {
        super(ID);

        Solstice.configManager.registerData(ID, NearConfig.class, NearConfig::new);
        Solstice.localeManager.registerModule(ID, NearLocale.MODULE);

        CommandRegistrationCallback.EVENT.register(NearCommand::new);
    }

    @Override
    public Collection<? extends ModCommand<?>> getCommands() {
        return List.of();
    }
}
