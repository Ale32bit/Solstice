package me.alexdevs.solstice.modules.announcement;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleProperties;
import me.alexdevs.solstice.api.module.ModuleProperties;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.modules.announcement.data.AnnouncementConfig;
import me.lucko.fabric.api.permissions.v0.Permissions;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;

import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AnnouncementModule extends ModuleBase {
    

    private ScheduledFuture<?> scheduledFuture = null;
    private int currentLine = 0;

    public AnnouncementModule(ModuleProperties properties) {
        super(properties);
    }

    @Override
    public void init() {
        registerConfig(AnnouncementConfig.class, AnnouncementConfig::new);

        SolsticeEvents.READY.register((instance, server) -> {
            setup();
        });

        SolsticeEvents.RELOAD.register(instance -> {
            if (scheduledFuture != null) {
                scheduledFuture.cancel(false);
            }
            setup();
        });
    }

    private void setup() {
        currentLine = 0;
        if (getConfig().enable) {
            scheduledFuture = Solstice.scheduler.scheduleAtFixedRate(this::announce, getConfig().delay, getConfig().delay, TimeUnit.SECONDS);
        }
    }

    public AnnouncementConfig getConfig() {
        return Solstice.configManager.getData(AnnouncementConfig.class);
    }

    public void announce() {
        var lines = getConfig().announcements;
        if (lines.isEmpty())
            return;

        if (getConfig().pickRandomly) {
            currentLine = new Random().nextInt(lines.size());
        }

        currentLine %= lines.size();
        var line = lines.get(currentLine);
        currentLine++;

        Solstice.server.getPlayerList().getPlayers().forEach(player -> {
            if (line.permission() != null) {
                var result = line.result();
                if (result == null)
                    result = true;
                if (Permissions.check(player, line.permission()) != result) {
                    return;
                }
            }
            var playerContext = PlaceholderContext.of(player);
            player.sendSystemMessage(Format.parse(line.text(), playerContext));
        });

    }
}
