package me.alexdevs.solstice.modules.tablist;

import com.mojang.authlib.GameProfile;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.api.text.RawPlaceholder;
import me.alexdevs.solstice.integrations.LuckPermsIntegration;
import me.alexdevs.solstice.modules.tablist.data.TabListConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TabListModule extends ModuleBase.Toggleable {
    private MinecraftServer server;
    private ScheduledFuture<?> scheduledFuture = null;

    public TabListModule(SolsticeIdentifier id) {
        super(id);
    }

    @Override
    public void init() {
        registerConfig(TabListConfig.class, TabListConfig::new);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.server = server;
            schedule();
        });

        SolsticeEvents.RELOAD.register(instance -> {
            if (scheduledFuture != null) {
                scheduledFuture.cancel(false);
            }
            schedule();
        });
    }

    public TabListConfig getConfig() {
        return Solstice.configManager.getData(TabListConfig.class);
    }

    private void schedule() {
        if (!isEnabled())
            return;

        var config = getConfig();
        if (!config.enable)
            return;

        scheduledFuture = Solstice.scheduler.scheduleAtFixedRate(this::updateTab, 0, config.delay, TimeUnit.MILLISECONDS);
    }

    public void updateTab() {
        if (!isEnabled())
            return;

        var config = getConfig();
        var period = Math.max(config.phasePeriod, 1);

        var phase = (float) (Math.sin((server.getTickCount() * Math.PI * 2) / period) + 1) / 2f;

        var placeholders = Map.of(
                "phase", String.valueOf(phase)
        );

        server.getPlayerList().getPlayers().forEach(player -> {
            var playerContext = PlaceholderContext.of(player);
            var header = RawPlaceholder.parse(String.join("\n", config.header), placeholders);
            var footer = RawPlaceholder.parse(String.join("\n", config.footer), placeholders);
            player.connection.send(new ClientboundTabListPacket(
                    Format.parse(header, playerContext),
                    Format.parse(footer, playerContext)
            ));
        });
    }

    /*public List<String> getGroupsOrder() {
        if (!isEnabled())
            return List.of();

        return getConfig().groupsOrder;
    }

    public ServerPlayer getPlayerByProfile(GameProfile profile) {
        return server.getPlayerList().getPlayer(profile.getId());
    }

    public List<ServerPlayer> getOrderedPlayerList() {
        var groups = getGroupsOrder();
        var groupOrder = new HashMap<String, Integer>();
        for (int i = 0; i < groups.size(); i++) {
            groupOrder.put(groups.get(i), i);
        }

        var list = server.getPlayerList().getPlayers().stream()
                .sorted(Comparator.comparingInt(player -> groupOrder.getOrDefault(
                        LuckPermsIntegration.getPrimaryGroup(player),
                        Integer.MAX_VALUE
                )))
                .toList();

        return list;
    }

    public void updateListOrder() {
        var list = getOrderedPlayerList();
    }*/
}
