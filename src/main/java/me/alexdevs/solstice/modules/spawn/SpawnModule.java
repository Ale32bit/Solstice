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
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class SpawnModule extends ModuleBase.Toggleable {
    public static final String ID = "spawn";

    public SpawnModule() {
        super(ID);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void init() {
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
                world.setDefaultSpawnPos(new BlockPos((int) legacy.getX(), (int) legacy.getY(), (int) legacy.getZ()), legacy.getYaw());
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
            var spawnPos = server.overworld().getSharedSpawnPos();
            spawnPosition = new ServerLocation(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0, 0, server.overworld());
        }
        return spawnPosition;
    }

    public ServerLevel getGlobalSpawnWorld() {
        var targetWorld = getConfig().globalSpawn.targetSpawnWorld;

        var key = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(targetWorld));
        return Solstice.server.getLevel(key);
    }

    public ServerLocation getGlobalSpawnPosition() {
        return getWorldSpawn(getGlobalSpawnWorld());
    }

    public ServerLocation getWorldSpawn(ServerLevel world) {
        var worldSpawnPosition = world.getSharedSpawnPos().getCenter();
        var worldSpawnYaw = world.getSharedSpawnAngle();
        var worldName = world.dimension().location().toString();

        if (world.dimension() != Level.OVERWORLD) {
            var spawnPoints = getServerData().spawnPoints;
            if (spawnPoints.containsKey(worldName)) {
                return spawnPoints.get(worldName);
            }
        }

        return new ServerLocation(
                worldSpawnPosition.x(), worldSpawnPosition.y(), worldSpawnPosition.z(),
                worldSpawnYaw, 0, world
        );
    }

    public SpawnConfig getConfig() {
        return Solstice.configManager.getData(SpawnConfig.class);
    }

    public SpawnServerData getServerData() {
        return Solstice.serverData.getData(SpawnServerData.class);
    }

    public void sendToSpawn(ServerPlayer player) {
        sendToSpawn(player, player.serverLevel());
    }

    public void sendToSpawn(ServerPlayer player, ServerLevel world) {
        var pos = getWorldSpawn(world);
        pos.teleport(player);
    }

    public @Nullable ServerLocation getFirstSpawn() {
        return Solstice.serverData.getData(SpawnServerData.class).firstSpawn;
    }
}
