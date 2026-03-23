package me.alexdevs.solstice.modules.styling;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.integrations.LuckPermsIntegration;
import me.alexdevs.solstice.modules.styling.data.StylingConfig;
import me.lucko.fabric.api.permissions.v0.Permissions;
//? if < 1.21.1 {
import net.minecraft.ChatFormatting;
//? }
import net.minecraft.network.chat.Component;
//? if < 1.21.1 {
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
//? }
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
//? if < 1.21.1 {
import net.minecraft.world.scores.Scoreboard;
import java.util.List;
import java.util.concurrent.TimeUnit;
//? }

public class StylingModule extends ModuleBase.Toggleable {
    public static final String ADVANCED_CHAT_FORMATTING_PERMISSION = "solstice.chat.advanced";
    public static final String LEGACY_CHAT_FORMATTING_PERMISSION = "solstice.chat.legacy";
    public static final String SILENT_ACTIVITY_PERMISSION = "solstice.chat.activity.silent";

    //? if >= 1.21.1 {
    /*private static final StylingConfig.NameplateFormat DEFAULT_NAMEPLATE = new StylingConfig.NameplateFormat("", "");
    *///? } else {
    private static final StylingConfig.NameplateFormat DEFAULT_NAMEPLATE = new StylingConfig.NameplateFormat("", "", "WHITE");
    //? }

    public StylingModule(ResourceLocation id) {
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
        });

        //? if >= 1.21.1 {
        /*SolsticeEvents.RELOAD.register(instance -> {
            var playerList = Solstice.server.getPlayerList();
            var scoreboard = Solstice.server.getScoreboard();
            for (var player : playerList.getPlayers()) {
                playerList.updateEntireScoreboard(scoreboard, player);
            }
        });
        *///? } else {
        SolsticeEvents.RELOAD.register(instance -> reloadNameplates(false));
        Solstice.scheduler.scheduleAtFixedRateSync(() -> reloadNameplates(false), 0, 1, TimeUnit.SECONDS);
        //? }
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

    //? if >= 1.21.1 {
    /*public boolean shouldColorNameplate() {
        return getConfig().doColorNameplate;
    }
    *///? } else {
    public ChatFormatting getNameplateColor(ServerPlayer player) {
        var config = getConfig();
        var primaryGroup = LuckPermsIntegration.getPrimaryGroup(player);
        var color = "WHITE";
        if (config.nameplateFormats.containsKey(primaryGroup)) {
            color = config.nameplateFormats.get(primaryGroup).color();
        } else {
            color = config.nameplateFormats.getOrDefault("default", DEFAULT_NAMEPLATE).color();
        }
        return ChatFormatting.getByName(color);
    }
    //? }

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

    //? if < 1.21.1 {
    private void reloadNameplates(boolean add) {
        var playerList = Solstice.server.getPlayerList();
        var scoreboard = Solstice.server.getScoreboard();
        for (var player : playerList.getPlayers()) {
            sendTeamSetup(player, playerList.getPlayers(), scoreboard, add);
        }
    }

    public void sendTeamSetup(ServerPlayer player, List<ServerPlayer> players, Scoreboard scoreboard, boolean add) {
        for (var otherPlayer : players) {
            if(otherPlayer == player) {
                continue;
            }
            var team = new CustomPlayerTeam(scoreboard, otherPlayer);
            player.connection.send(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, add));
        }
    }
    //? }
}
