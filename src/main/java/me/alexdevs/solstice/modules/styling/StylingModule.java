package me.alexdevs.solstice.modules.styling;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.styling.data.StylingConfig;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import me.alexdevs.solstice.api.text.Format;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

public class StylingModule extends ModuleBase.Toggleable {
    public static final String ID = "styling";
    public static final String ADVANCED_CHAT_FORMATTING_PERMISSION = "solstice.chat.advanced";
    public static final String SILENT_ACTIVITY_PERMISSION = "solstice.chat.activity.silent";
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

    public static boolean shouldSendActivityMessage(ServerPlayer player) {
        return !Permissions.check(player, SILENT_ACTIVITY_PERMISSION);
    }

    public static void broadcastActivity(PlayerList playerList, Component component, boolean bypassHiddenMessage) {
        for(var player : playerList.getPlayers()) {
            if(shouldSendActivityMessage(player)) {
                player.sendSystemMessage(component, bypassHiddenMessage);
            }
        }
    }
}
