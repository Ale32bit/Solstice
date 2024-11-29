package me.alexdevs.solstice.core;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.util.Format;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AutoAnnouncements {
    private static ScheduledFuture<?> scheduledFuture = null;
    private static int currentLine = 0;
    private static MinecraftServer server;

    public static void register() {
        SolsticeEvents.RELOAD.register(instance -> {
            if (scheduledFuture != null) {
                scheduledFuture.cancel(false);
            }
            setup();
        });

        ServerLifecycleEvents.SERVER_STARTED.register(mcServer -> {
            server = mcServer;
            setup();
        });
    }

    public static void announce() {
        var lines = Solstice.config().autoAnnouncements.announcements;
        if (lines.isEmpty())
            return;

        if (Solstice.config().autoAnnouncements.pickRandomly) {
            currentLine = new Random().nextInt(lines.size());
        }

        currentLine %= lines.size();
        var line = lines.get(currentLine);
        currentLine++;

        server.getPlayerManager().getPlayerList().forEach(player -> {
            if(line.permission() != null) {
                var result = line.result();
                if(result == null)
                    result = true;
                if(Permissions.check(player, line.permission()) != result) {
                    return;
                }
            }
            var playerContext = PlaceholderContext.of(player);
            player.sendMessage(Format.parse(line.text(), playerContext));
        });

    }

    private static void setup() {
        currentLine = 0;
        if (Solstice.config().autoAnnouncements.enableAnnouncements) {
            var delay = Solstice.config().autoAnnouncements.delay;
            scheduledFuture = Solstice.scheduler.scheduleAtFixedRate(AutoAnnouncements::announce, delay, delay, TimeUnit.SECONDS);
        }
    }
}
