package me.alexdevs.solstice.modules.fly;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.fly.commands.FlyCommand;
import me.alexdevs.solstice.modules.fly.data.FlyLocale;
import me.alexdevs.solstice.modules.fly.data.FlyPlayerData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class FlyModule extends ModuleBase.Toggleable {
    public static final String ID = "fly";

    public FlyModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.localeManager.registerModule(ID, FlyLocale.MODULE);
        Solstice.playerData.registerData(ID, FlyPlayerData.class, FlyPlayerData::new);

        commands.add(new FlyCommand(this));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var player = handler.getPlayer();

            var data = Solstice.playerData.get(player).getData(FlyPlayerData.class);
            if(data.flightEnabled) {
                var abilities = player.getAbilities();
                abilities.allowFlying = true;
                player.sendAbilitiesUpdate();
            }
        });
    }
}
