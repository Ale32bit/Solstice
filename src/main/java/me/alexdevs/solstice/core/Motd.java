package me.alexdevs.solstice.core;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.util.Format;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.text.Text;

public class Motd {
    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if(Solstice.config().motd.enableMotd) {
                var motd = buildMotd(PlaceholderContext.of(handler.getPlayer()));
                handler.getPlayer().sendMessage(motd);
            }
        });
    }

    public static Text buildMotd(PlaceholderContext context) {
        var motd = String.join("\n", Solstice.config().motd.motdLines);

        return Format.parse(motd, context);
    }
}
