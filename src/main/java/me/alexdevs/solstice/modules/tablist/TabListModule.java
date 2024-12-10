package me.alexdevs.solstice.modules.tablist;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.modules.tablist.data.TabListConfig;
import me.alexdevs.solstice.util.Format;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TabListModule {
    public static final String ID = "tablist";

    private MinecraftServer server;
    private ScheduledFuture<?> scheduledFuture = null;

    public TabListModule() {
        Solstice.newConfigManager.registerData(ID, TabListConfig.class, TabListConfig::new);

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

    private void schedule() {
        var config = Solstice.newConfigManager.getData(TabListConfig.class);
        if (!config.enable)
            return;

        scheduledFuture = Solstice.scheduler.scheduleAtFixedRate(this::updateTab, 0, config.delay, TimeUnit.MILLISECONDS);
    }

    public void updateTab() {
        var config = Solstice.newConfigManager.getData(TabListConfig.class);
        var period = Math.max(config.phasePeriod, 1);

        var phase = (Math.sin((server.getTicks() * Math.PI * 2) / period) + 1) / 2d;

        var placeholders = Map.of(
                "phase", Text.of(String.valueOf(phase))
        );

        server.getPlayerManager().getPlayerList().forEach(player -> {
            var playerContext = PlaceholderContext.of(player);
            var header = String.join("\n", config.header);
            var footer = String.join("\n", config.footer);
            player.networkHandler.sendPacket(new PlayerListHeaderS2CPacket(
                    Format.parse(header, playerContext, placeholders),
                    Format.parse(footer, playerContext, placeholders)
            ));
        });
    }
}
