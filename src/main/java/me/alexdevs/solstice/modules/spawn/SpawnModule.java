package me.alexdevs.solstice.modules.spawn;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.spawn.commands.DeleteSpawnCommand;
import me.alexdevs.solstice.modules.spawn.commands.SetSpawnCommand;
import me.alexdevs.solstice.modules.spawn.commands.SpawnCommand;
import me.alexdevs.solstice.modules.spawn.data.SpawnLocale;
import me.alexdevs.solstice.modules.spawn.data.SpawnServerData;

public class SpawnModule extends ModuleBase {
    public static final String ID = "spawn";

    public SpawnModule() {
        super(ID);

        Solstice.localeManager.registerModule(ID, SpawnLocale.MODULE);
        Solstice.serverData.registerData(ID, SpawnServerData.class, SpawnServerData::new);

        commands.add(new SpawnCommand(this));
        commands.add(new SetSpawnCommand(this));
        commands.add(new DeleteSpawnCommand(this));

        SolsticeEvents.WELCOME.register((player, server) -> {
            var serverData = Solstice.serverData.getData(SpawnServerData.class);
            var spawnPosition = serverData.spawn;
            if (spawnPosition != null) {
                spawnPosition.teleport(player, false);
            }
        });
    }
}
