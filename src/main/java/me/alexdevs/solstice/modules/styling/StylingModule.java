package me.alexdevs.solstice.modules.styling;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.api.utils.PlayerUtils;
import me.alexdevs.solstice.integrations.LuckPermsIntegration;
import me.alexdevs.solstice.modules.styling.data.StylingConfig;
import me.lucko.fabric.api.permissions.v0.Permissions;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.scores.PlayerTeam;

public class StylingModule extends ModuleBase.Toggleable {
    public static final String ADVANCED_CHAT_FORMATTING_PERMISSION = "solstice.chat.advanced";
    public static final String LEGACY_CHAT_FORMATTING_PERMISSION = "solstice.chat.legacy";
    public static final String SILENT_ACTIVITY_PERMISSION = "solstice.chat.activity.silent";

    private static final StylingConfig.NameplateFormat DEFAULT_NAMEPLATE = new StylingConfig.NameplateFormat("", "", "WHITE");

    public StylingModule(SolsticeIdentifier id) {
        super(id);
    }

    @SuppressWarnings("deprecation")
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

            // Cleanup
            var scoreboard = server.getScoreboard();
            for (var team : scoreboard.getPlayerTeams()) {
                if (team.getName().startsWith("sol_")) {
                    scoreboard.removePlayerTeam(team);
                }
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var player = handler.getPlayer();
            if (player.getTeam() == null) {
                ServerScoreboard scoreboard = server.getScoreboard();
                var username = PlayerUtils.getName(player.getGameProfile());
                PlayerTeam team = scoreboard.addPlayerTeam("sol_" + username);
                team.setDisplayName(player.getDisplayName());
                //? if >= 26.1
                //team.setColor(java.util.Optional.ofNullable(this.getNameplateColor(player)).map(c -> net.minecraft.world.scores.TeamColor.valueOf(c.name())));
                //? if < 26.1
                team.setColor(this.getNameplateColor(player));
                team.setPlayerPrefix(this.getNameplatePrefix(player));
                team.setPlayerSuffix(this.getNameplateSuffix(player));
                scoreboard.addPlayerToTeam(username, team);
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            var player = handler.getPlayer();
            var username = PlayerUtils.getName(player.getGameProfile());
            var scoreboard = server.getScoreboard();
            var team = scoreboard.getPlayerTeam("sol_" + username);
            if (team != null) {
                scoreboard.removePlayerTeam(team);
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

    public boolean shouldSendActivityMessage(ServerPlayer player) {
        return !Permissions.check(player, SILENT_ACTIVITY_PERMISSION);
    }

    public void broadcastActivity(PlayerList playerList, Component component, boolean bypassHiddenMessage) {
        for (var player : playerList.getPlayers()) {
            if (shouldSendActivityMessage(player)) {
                player.sendSystemMessage(component, bypassHiddenMessage);
            }
        }
    }

    public ChatFormatting getNameplateColor(ServerPlayer player) {
        var config = this.getConfig();
        var primaryGroup = LuckPermsIntegration.getPrimaryGroup(player);
        var color = "WHITE";
        if (config.nameplateFormats.containsKey(primaryGroup)) {
            color = config.nameplateFormats.get(primaryGroup).color();
        } else {
            color = config.nameplateFormats.getOrDefault("default", DEFAULT_NAMEPLATE).color();
        }

        //? if >= 26.1
        //return ChatFormatting.valueOf(color);
        //? if < 26.1
        return ChatFormatting.getByName(color);
    }

    public Component getNameplatePrefix(ServerPlayer player) {
        var config = getConfig();
        var primaryGroup = LuckPermsIntegration.getPrimaryGroup(player);
        var format = "";
        if (config.nameplateFormats.containsKey(primaryGroup)) {
            format = config.nameplateFormats.get(primaryGroup).prefix();
        } else {
            format = config.nameplateFormats.getOrDefault("default", DEFAULT_NAMEPLATE).prefix();
        }

        return Format.parse(format, PlaceholderContext.of(player));
    }

    public Component getNameplateSuffix(ServerPlayer player) {
        var config = getConfig();
        var primaryGroup = LuckPermsIntegration.getPrimaryGroup(player);
        var format = "";
        if (config.nameplateFormats.containsKey(primaryGroup)) {
            format = config.nameplateFormats.get(primaryGroup).suffix();
        } else {
            format = config.nameplateFormats.getOrDefault("default", DEFAULT_NAMEPLATE).suffix();
        }

        return Format.parse(format, PlaceholderContext.of(player));
    }
}
