package me.alexdevs.solstice.modules.spawn;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.spawn.commands.FirstSpawnCommand;
import me.alexdevs.solstice.modules.spawn.commands.SetFirstSpawnCommand;
import me.alexdevs.solstice.modules.spawn.commands.SetSpawnCommand;
import me.alexdevs.solstice.modules.spawn.commands.SpawnCommand;
import me.alexdevs.solstice.modules.spawn.data.SpawnConfig;
import me.alexdevs.solstice.modules.spawn.data.SpawnLocale;
import me.alexdevs.solstice.modules.spawn.data.SpawnServerData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class SpawnModule extends ModuleBase {
    public static final String ID = "spawn";

    @SuppressWarnings("deprecation")
    public SpawnModule() {
        super(ID);

        Solstice.localeManager.registerModule(ID, SpawnLocale.MODULE);
        Solstice.serverData.registerData(ID, SpawnServerData.class, SpawnServerData::new);
        Solstice.configManager.registerData(ID, SpawnConfig.class, SpawnConfig::new);

        commands.add(new SpawnCommand(this));
        commands.add(new SetSpawnCommand(this));
        commands.add(new FirstSpawnCommand(this));
        commands.add(new SetFirstSpawnCommand(this));

        SolsticeEvents.WELCOME.register((player, server) -> {
            var firstSpawn = getFirstSpawn();
            if (firstSpawn != null) {
                // Send next tick, twice, so it does not conflict with "on-login" spawn setting.
                Solstice.nextTick(() -> Solstice.nextTick(() -> firstSpawn.teleport(player)));
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var config = getConfig();
            if (config.globalSpawn.onLogin) {
                Solstice.nextTick(() -> {
                    getGlobalSpawnPosition().teleport(handler.getPlayer(), false);
                });
            }
        });

        SolsticeEvents.READY.register((instance, server) -> {
            var spawnData = getServerData();
            if (spawnData.spawn != null) {
                var legacy = spawnData.spawn;
                var world = legacy.getWorld(server);
                world.setSpawnPos(new BlockPos((int) legacy.getX(), (int) legacy.getY(), (int) legacy.getZ()), legacy.getYaw());
                spawnData.spawn = null;
            }
        });
    }

    @Deprecated
    public ServerLocation getSpawn() {
        var serverData = getServerData();
        var spawnPosition = serverData.spawn;
        if (spawnPosition == null) {
            var server = Solstice.server;
            var spawnPos = server.getOverworld().getSpawnPos();
            spawnPosition = new ServerLocation(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0, 0, server.getOverworld());
        }
        return spawnPosition;
    }

    public ServerWorld getGlobalSpawnWorld() {
        var targetWorld = getConfig().globalSpawn.targetSpawnWorld;

        var key = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(targetWorld));
        return Solstice.server.getWorld(key);
    }

    public ServerLocation getGlobalSpawnPosition() {
        var world = getGlobalSpawnWorld();
        var worldSpawnPos = world.getSpawnPos().toCenterPos();
        var worldSpawnRot = world.getSpawnAngle();
        return new ServerLocation(
                worldSpawnPos.getX(), worldSpawnPos.getY(), worldSpawnPos.getZ(), worldSpawnRot, 0, world
        );
    }

    public ServerLocation getWorldSpawn(ServerWorld world) {
        var spawnPos = world.getSpawnPos().toCenterPos();
        var yaw = world.getSpawnAngle();
        return new ServerLocation(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), yaw, 0, world);
    }

    public SpawnConfig getConfig() {
        return Solstice.configManager.getData(SpawnConfig.class);
    }

    public SpawnServerData getServerData() {
        return Solstice.serverData.getData(SpawnServerData.class);
    }

    public void sendToSpawn(ServerPlayerEntity player) {
        sendToSpawn(player, player.getServerWorld());
    }

    public void sendToSpawn(ServerPlayerEntity player, ServerWorld world) {
        var pos = getWorldSpawn(world);
        pos.teleport(player);
    }

    public @Nullable ServerLocation getFirstSpawn() {
        return Solstice.serverData.getData(SpawnServerData.class).firstSpawn;
    }
}
