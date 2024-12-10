package me.alexdevs.solstice.modules.autoannouncement;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.util.Format;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AutoAnnouncementModule {
    public static final String ID = "autoannouncement";

    private ScheduledFuture<?> scheduledFuture = null;
    private int currentLine = 0;
    private MinecraftServer server;

    private AutoAnnouncementConfig config;

    public AutoAnnouncementModule() {
        Solstice.newConfigManager.registerData(ID, AutoAnnouncementConfig.class, AutoAnnouncementConfig::new);

        SolsticeEvents.RELOAD.register(instance -> {
            if (scheduledFuture != null) {
                scheduledFuture.cancel(false);
            }
            setup();
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.server = server;
            setup();
        });
    }

    public void announce() {
        var lines = config.announcements;
        if (lines.isEmpty())
            return;

        if (config.pickRandomly) {
            currentLine = new Random().nextInt(lines.size());
        }

        currentLine %= lines.size();
        var line = lines.get(currentLine);
        currentLine++;

        server.getPlayerManager().getPlayerList().forEach(player -> {
            if (line.permission() != null) {
                var result = line.result();
                if (result == null)
                    result = true;
                if (Permissions.check(player, line.permission()) != result) {
                    return;
                }
            }
            var playerContext = PlaceholderContext.of(player);
            player.sendMessage(Format.parse(line.text(), playerContext));
        });

    }

    private void setup() {
        this.config = Solstice.newConfigManager.getData(AutoAnnouncementConfig.class);
        currentLine = 0;
        if (config.enable) {
            scheduledFuture = Solstice.scheduler.scheduleAtFixedRate(this::announce, config.delay, config.delay, TimeUnit.SECONDS);
        }
    }
}
