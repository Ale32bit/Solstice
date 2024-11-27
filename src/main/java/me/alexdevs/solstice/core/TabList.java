package me.alexdevs.solstice.core;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.util.Format;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TabList {
    private static MinecraftServer server;
    private static ScheduledFuture<?> scheduledFuture = null;

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(mcServer -> {
            server = mcServer;
            schedule();
        });

        SolsticeEvents.RELOAD.register(instance -> {
            if (scheduledFuture != null) {
                scheduledFuture.cancel(false);
            }
            schedule();
        });
    }

    private static void schedule() {
        if (!Solstice.config().customTabList.enableTabList)
            return;

        scheduledFuture = Solstice.scheduler.scheduleAtFixedRate(TabList::updateTab, 0, Solstice.config().customTabList.tabListDelay, TimeUnit.MILLISECONDS);
    }

    public static void updateTab() {
        var period = Math.max(Solstice.config().customTabList.tabPhasePeriod, 1);

        var phase = (Math.sin((server.getTicks() * Math.PI * 2) / period) + 1) / 2d;

        var placeholders = Map.of(
                "phase", Text.of(String.valueOf(phase))
        );

        server.getPlayerManager().getPlayerList().forEach(player -> {
            var playerContext = PlaceholderContext.of(player);
            var header = String.join("\n", Solstice.config().customTabList.tabHeader);
            var footer = String.join("\n", Solstice.config().customTabList.tabFooter);
            player.networkHandler.sendPacket(new PlayerListHeaderS2CPacket(
                    Format.parse(header, playerContext, placeholders),
                    Format.parse(footer, playerContext, placeholders)
            ));
        });
    }

}
