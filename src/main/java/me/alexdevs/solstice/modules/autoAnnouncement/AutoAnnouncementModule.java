package me.alexdevs.solstice.modules.autoAnnouncement;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.modules.autoAnnouncement.data.AutoAnnouncementConfig;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.resources.ResourceLocation;

import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AutoAnnouncementModule extends ModuleBase.Toggleable {
    

    private ScheduledFuture<?> scheduledFuture = null;
    private int currentLine = 0;

    public AutoAnnouncementModule(ResourceLocation id) {
        super(id);
    }

    @Override
    public void init() {
        registerConfig(AutoAnnouncementConfig.class, AutoAnnouncementConfig::new);

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

    public AutoAnnouncementConfig getConfig() {
        return Solstice.configManager.getData(AutoAnnouncementConfig.class);
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
