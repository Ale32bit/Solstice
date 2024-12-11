package me.alexdevs.solstice.modules.spawn;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.modules.spawn.commands.DelSpawnCommand;
import me.alexdevs.solstice.modules.spawn.commands.SetSpawnCommand;
import me.alexdevs.solstice.modules.spawn.commands.SpawnCommand;
import me.alexdevs.solstice.modules.spawn.data.SpawnLocale;
import me.alexdevs.solstice.modules.spawn.data.SpawnServerData;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class SpawnModule {
    public static final String ID = "spawn";

    public SpawnModule() {
        Solstice.localeManager.registerModule(ID, SpawnLocale.MODULE);
        Solstice.serverData.registerData(ID, SpawnServerData.class, SpawnServerData::new);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            new SpawnCommand(dispatcher, registryAccess, environment);
            new SetSpawnCommand(dispatcher, registryAccess, environment);
            new DelSpawnCommand(dispatcher, registryAccess, environment);
        });

        SolsticeEvents.WELCOME.register((player, server) -> {
            var serverData = Solstice.serverData.getData(SpawnServerData.class);
            var spawnPosition = serverData.spawn;
            if (spawnPosition != null) {
                spawnPosition.teleport(player, false);
            }
        });
    }
}
