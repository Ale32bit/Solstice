package me.alexdevs.solstice.modules.god;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.proxy.ProxyServerPlayConnectionEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.god.commands.GodCommand;
import me.alexdevs.solstice.modules.god.data.GodLocale;
import me.alexdevs.solstice.modules.god.data.GodPlayerData;

public class GodModule extends ModuleBase.Toggleable {
    public static final String ID = "god";

    public GodModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.localeManager.registerModule(ID, GodLocale.MODULE);
        Solstice.playerData.registerData(ID, GodPlayerData.class, GodPlayerData::new);

        commands.add(new GodCommand(this));

        ProxyServerPlayConnectionEvents.JOIN.register((player, server) -> {
            var data = Solstice.playerData.get(player).getData(GodPlayerData.class);

            if (data.invulnerabilityEnabled) {
                var abilities = player.getAbilities();
                abilities.invulnerable = true;
                player.onUpdateAbilities();
            }
        });
    }
}
