package me.alexdevs.solstice.modules.styling;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.integrations.LuckPermsIntegration;
import me.alexdevs.solstice.modules.styling.data.StylingConfig;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

public class StylingModule extends ModuleBase.Toggleable {
    public static final String ADVANCED_CHAT_FORMATTING_PERMISSION = "solstice.chat.advanced";
    public static final String LEGACY_CHAT_FORMATTING_PERMISSION = "solstice.chat.legacy";
    public static final String SILENT_ACTIVITY_PERMISSION = "solstice.chat.activity.silent";

    public StylingModule(ResourceLocation id) {
        super(id);
    }

    @Override
    public void init() {
        Solstice.configManager.registerData(getId(), StylingConfig.class, StylingConfig::new);

        SolsticeEvents.WELCOME.register((player, server) -> {
            var config = getConfig();
            if (config.welcomeNewPlayers) {
                var playerContext = PlaceholderContext.of(player);
                Solstice.nextTick(() -> {
                    Solstice.getInstance().broadcast(Format.parse(getConfig().welcome, playerContext));
                });
            }
        });

        SolsticeEvents.READY.register((instance, server) -> {
            var config = getConfig();
            if (config.chatFormat != null) {
                config.chatFormats.put("default", config.chatFormat);
                config.chatFormat = null;
                Solstice.configManager.save();
            }
        });
    }

    public StylingConfig getConfig() {
        return Solstice.configManager.getData(StylingConfig.class);
    }

    public String getChatFormat(ServerPlayer player) {
        var format = "<%player:displayname%> ${message}";
        if (!this.isEnabled())
            return format;

        var config = getConfig();
        var primaryGroup = LuckPermsIntegration.getPrimaryGroup(player);
        if (config.chatFormats.containsKey(primaryGroup)) {
            format = config.chatFormats.get(primaryGroup);
        } else {
            format = config.chatFormats.getOrDefault("default", format);
        }

        return format;
    }

    public static boolean shouldSendActivityMessage(ServerPlayer player) {
        return !Permissions.check(player, SILENT_ACTIVITY_PERMISSION);
    }

    public static void broadcastActivity(PlayerList playerList, Component component, boolean bypassHiddenMessage) {
        for (var player : playerList.getPlayers()) {
            if (shouldSendActivityMessage(player)) {
                player.sendSystemMessage(component, bypassHiddenMessage);
            }
        }
    }
}
