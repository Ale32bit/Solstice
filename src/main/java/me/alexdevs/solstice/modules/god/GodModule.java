package me.alexdevs.solstice.modules.god;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.god.commands.GodCommand;
import me.alexdevs.solstice.modules.god.data.GodLocale;
import me.alexdevs.solstice.modules.god.data.GodPlayerData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.resources.ResourceLocation;
public class GodModule extends ModuleBase.Toggleable {
    

    public GodModule(ResourceLocation id) {
        super(id);
    }

    @Override
    public void init() {
        registerLocale(GodLocale.MODULE);
        registerPlayerData(GodPlayerData.class, GodPlayerData::new);

        commands.add(new GodCommand(this));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var player = handler.getPlayer();

            var data = Solstice.playerData.get(player).getData(GodPlayerData.class);
            if(data.invulnerabilityEnabled) {
                var abilities = player.getAbilities();
                abilities.invulnerable = true;
                player.onUpdateAbilities();
            }
        });
    }
}
