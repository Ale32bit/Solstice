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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

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
            var firstSpawn = getFirstSpawn();
            if (firstSpawn != null) {
                firstSpawn.teleport(player);
            }
        });
    }

    @Deprecated
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

    public ServerPosition getWorldSpawn(ServerWorld world) {
        var spawnPos = world.getSpawnPos();
        var yaw = world.getSpawnAngle();
        return new ServerPosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), yaw, 0, world);
    }

    public boolean forceOnDeath() {
        var config = Solstice.configManager.getData(SpawnConfig.class);
        return config.forceOnDeath;
    }

    public void sendToSpawn(ServerPlayerEntity player) {
        sendToSpawn(player, player.getServerWorld());
    }

    public void sendToSpawn(ServerPlayerEntity player, ServerWorld world) {
        var pos = getWorldSpawn(world);
        pos.teleport(player);
    }

    public @Nullable ServerPosition getFirstSpawn() {
        return Solstice.serverData.getData(SpawnServerData.class).firstSpawn;
    }
}
