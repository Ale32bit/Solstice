package me.alexdevs.solstice.modules.core;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.locale.Locale;
import me.alexdevs.solstice.modules.core.commands.SolsticeCommand;
import me.alexdevs.solstice.util.data.GsonDataManager;
import me.alexdevs.solstice.util.data.HoconDataManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.text.Text;

import java.util.Map;

public class CoreModule {
    public static final String ID = "core";
    private final Locale locale = Solstice.newLocaleManager.getLocale(ID);

    public CoreModule() {
        Solstice.newLocaleManager.registerShared(CoreLocale.SHARED);

        CommandRegistrationCallback.EVENT.register((dispatcher, registry, environment) -> {
            new SolsticeCommand(dispatcher, registry, environment);
        });
    }
}
