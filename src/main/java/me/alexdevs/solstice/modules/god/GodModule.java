package me.alexdevs.solstice.modules.god;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.fly.data.FlyPlayerData;
import me.alexdevs.solstice.modules.god.commands.GodCommand;
import me.alexdevs.solstice.modules.god.data.GodLocale;
import me.alexdevs.solstice.modules.god.data.GodPlayerData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class GodModule extends ModuleBase {
    public static final String ID = "god";

    public GodModule() {
        super(ID);

        Solstice.localeManager.registerModule(ID, GodLocale.MODULE);
        Solstice.playerData.registerData(ID, GodPlayerData.class, GodPlayerData::new);

        commands.add(new GodCommand(this));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var player = handler.getPlayer();

            var data = Solstice.playerData.get(player).getData(GodPlayerData.class);
            if(data.invulnerabilityEnabled) {
                var abilities = player.getAbilities();
                abilities.invulnerable = true;
                player.sendAbilitiesUpdate();
            }
        });
    }
}
