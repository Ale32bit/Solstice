package me.alexdevs.solstice.modules.fly;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.fly.commands.FlyCommand;
import me.alexdevs.solstice.modules.fly.data.FlyLocale;
import me.alexdevs.solstice.modules.fly.data.FlyPlayerData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.resources.ResourceLocation;
public class FlyModule extends ModuleBase.Toggleable {
    

    public FlyModule(ResourceLocation id) {
        super(id);
    }

    @Override
    public void init() {
        registerLocale(FlyLocale.MODULE);
        registerPlayerData(FlyPlayerData.class, FlyPlayerData::new);

        commands.add(new FlyCommand(this));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var player = handler.getPlayer();

            var data = Solstice.playerData.get(player).getData(FlyPlayerData.class);
            if(data.flightEnabled) {
                var abilities = player.getAbilities();
                abilities.mayfly = true;
                player.onUpdateAbilities();
            }
        });
    }
}
