package me.alexdevs.solstice.modules.tablist;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.events.proxy.ProxyServerLifecycleEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.api.text.RawPlaceholder;
import me.alexdevs.solstice.modules.tablist.data.TabListConfig;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.server.MinecraftServer;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TabListModule extends ModuleBase.Toggleable {
    public static final String ID = "tablist";

    private MinecraftServer server;
    private ScheduledFuture<?> scheduledFuture = null;

    public TabListModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.configManager.registerData(ID, TabListConfig.class, TabListConfig::new);

        ProxyServerLifecycleEvents.SERVER_STARTED.register(server -> {
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
        var config = Solstice.configManager.getData(TabListConfig.class);
        if (!config.enable)
            return;

        scheduledFuture = Solstice.scheduler.scheduleAtFixedRate(
                this::updateTab,
                0,
                config.delay,
                TimeUnit.MILLISECONDS
        );
    }

    public void updateTab() {
        var config = Solstice.configManager.getData(TabListConfig.class);
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
}
