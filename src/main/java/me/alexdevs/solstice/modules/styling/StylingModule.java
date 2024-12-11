package me.alexdevs.solstice.modules.styling;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.styling.data.StylingConfig;
import me.alexdevs.solstice.modules.styling.data.StylingLocale;

public class StylingModule {
    public static final String ID = "styling";
    public static final String ADVANCED_CHAT_FORMATTING_PERMISSION = "solstice.chat.advanced";
    public StylingModule() {
        Solstice.configManager.registerData(ID, StylingConfig.class, StylingConfig::new);
        Solstice.localeManager.registerModule(ID, StylingLocale.MODULE);
    }
}
