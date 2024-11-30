package me.alexdevs.solstice.core;

import me.alexdevs.solstice.Solstice;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.text.Text;

public class Motd {
    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if(Solstice.config().motd.enableMotd) {
                if(!InfoPages.exists("motd")) {
                    Solstice.LOGGER.warn("Could not send MOTD because info/motd.txt does not exist!");
                    return;
                }
                var motd = buildMotd(PlaceholderContext.of(handler.getPlayer()));
                handler.getPlayer().sendMessage(motd);
            }
        });
    }

    public static Text buildMotd(PlaceholderContext context) {
        return InfoPages.getPage("motd", context);
    }
}
