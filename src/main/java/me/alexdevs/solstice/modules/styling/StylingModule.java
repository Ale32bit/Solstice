package me.alexdevs.solstice.modules.styling;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.modules.styling.data.StylingConfig;
import me.alexdevs.solstice.modules.styling.data.StylingLocale;

public class StylingModule {
    public static final String ID = "styling";
    public static final String ADVANCED_CHAT_FORMATTING_PERMISSION = "solstice.chat.advanced";
    public StylingModule() {
        Solstice.configManager.registerData(ID, StylingConfig.class, StylingConfig::new);
        Solstice.localeManager.registerModule(ID, StylingLocale.MODULE);

        SolsticeEvents.WELCOME.register((player, server) -> {
            var config = Solstice.configManager.getData(StylingConfig.class);
            if(config.welcomeNewPlayers) {
                var playerContext = PlaceholderContext.of(player);
                var locale = Solstice.localeManager.getLocale(ID);
                Solstice.nextTick(() -> {
                    Solstice.getInstance().broadcast(locale.get("welcome", playerContext));
                });
            }
        });
    }
}
