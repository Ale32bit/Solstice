package me.alexdevs.solstice.modules.spawn;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerPosition;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.spawn.commands.DeleteSpawnCommand;
import me.alexdevs.solstice.modules.spawn.commands.SetSpawnCommand;
import me.alexdevs.solstice.modules.spawn.commands.SpawnCommand;
import me.alexdevs.solstice.modules.spawn.data.SpawnConfig;
import me.alexdevs.solstice.modules.spawn.data.SpawnLocale;
import me.alexdevs.solstice.modules.spawn.data.SpawnServerData;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class SpawnModule extends ModuleBase {
    public static final String ID = "spawn";

    public SpawnModule() {
        super(ID);

        Solstice.localeManager.registerModule(ID, SpawnLocale.MODULE);
        Solstice.serverData.registerData(ID, SpawnServerData.class, SpawnServerData::new);
        Solstice.configManager.registerData(ID, SpawnConfig.class, SpawnConfig::new);

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

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if (forceOnDeath()) {
                //sendToSpawn(newPlayer);
            }
        });
    }

    public ServerPosition getSpawn() {
        var serverData = Solstice.serverData.getData(SpawnServerData.class);
        var spawnPosition = serverData.spawn;
        if (spawnPosition == null) {
            var server = Solstice.server;
            var spawnPos = server.getOverworld().getSpawnPos();
            spawnPosition = new ServerPosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0, 0, server.getOverworld());
        }
        return spawnPosition;
    }

    public boolean forceOnDeath() {
        var config = Solstice.configManager.getData(SpawnConfig.class);
        return config.forceOnDeath;
    }

    public void sendToSpawn(ServerPlayerEntity player) {
        getSpawn().teleport(player);
    }
}
