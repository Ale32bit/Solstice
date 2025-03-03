package me.alexdevs.solstice.modules.styling;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.styling.data.StylingConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import me.alexdevs.solstice.api.text.Format;

public class StylingModule extends ModuleBase.Toggleable {
    public static final String ID = "styling";
    public static final String ADVANCED_CHAT_FORMATTING_PERMISSION = "solstice.chat.advanced";
    public static final ResourceKey<ChatType> CHAT_TYPE = ResourceKey.create(Registries.CHAT_TYPE, ResourceLocation.fromNamespaceAndPath(Solstice.MOD_ID, "chat"));

    public StylingModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.configManager.registerData(ID, StylingConfig.class, StylingConfig::new);

        SolsticeEvents.WELCOME.register((player, server) -> {
            var config = Solstice.configManager.getData(StylingConfig.class);
            if (config.welcomeNewPlayers) {
                var playerContext = PlaceholderContext.of(player);
                Solstice.nextTick(() -> {
                    Solstice.getInstance().broadcast(Format.parse(getConfig().welcome, playerContext));
                });
            }
        });
    }

    public StylingConfig getConfig() {
        return Solstice.configManager.getData(StylingConfig.class);
    }
}
