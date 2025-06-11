package me.alexdevs.solstice.fabric;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.proxy.*;
import me.alexdevs.solstice.api.platform.ModInfo;
import me.alexdevs.solstice.api.platform.PlatformHelper;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class FabricPlatform extends PlatformHelper {
    public void init() {
        CommandRegistrationCallback.EVENT.register(ProxyCommandRegistrationCallback.EVENT.invoker()::onRegister);
        ServerLifecycleEvents.SERVER_STARTING.register(ProxyServerLifecycleEvents.SERVER_STARTING.invoker()::onServerStarting);
        ServerLifecycleEvents.SERVER_STARTED.register(ProxyServerLifecycleEvents.SERVER_STARTED.invoker()::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(ProxyServerLifecycleEvents.SERVER_STOPPING.invoker()::onServerStopping);
        ServerLifecycleEvents.SERVER_STOPPED.register(ProxyServerLifecycleEvents.SERVER_STOPPED.invoker()::onServerStopped);
        ServerTickEvents.START_SERVER_TICK.register(ProxyServerTickEvents.START_SERVER_TICK.invoker()::onStartTick);
        ServerTickEvents.END_SERVER_TICK.register(ProxyServerTickEvents.END_SERVER_TICK.invoker()::onEndTick);
        UseItemCallback.EVENT.register(ProxyUseItemCallback.EVENT.invoker()::interact);
        UseBlockCallback.EVENT.register(ProxyUseBlockCallback.EVENT.invoker()::interact);
        UseEntityCallback.EVENT.register(ProxyUseEntityCallback.EVENT.invoker()::interact);
        PlayerBlockBreakEvents.BEFORE.register(ProxyPlayerBlockBreakEvents.BEFORE.invoker()::beforeBlockBreak);
        AttackBlockCallback.EVENT.register(ProxyAttackBlockCallback.EVENT.invoker()::interact);
        AttackEntityCallback.EVENT.register(ProxyAttackEntityCallback.EVENT.invoker()::interact);

        ServerPlayConnectionEvents.JOIN.register((handler, _sender, server) -> ProxyServerPlayConnectionEvents.JOIN.invoker()
                .onJoin(handler.getPlayer(), server));

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> ProxyServerPlayConnectionEvents.DISCONNECT.invoker()
                .onDisconnect(handler.getPlayer(), server));

        EntitySleepEvents.ALLOW_SLEEP_TIME.register(ProxyEntitySleepEvents.ALLOW_SLEEP_TIME.invoker()::allowSleepTime);
    }

    @Override
    public Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    @Override
    public boolean isNativeForge() {
        return false;
    }

    @Override
    public Object getModContainer() {
        return FabricLoader.getInstance().getModContainer(Solstice.MOD_ID).orElseThrow();
    }

    @Override
    public String getModVersion() {
        return FabricLoader.getInstance()
                .getModContainer(Solstice.MOD_ID)
                .orElseThrow()
                .getMetadata()
                .getVersion()
                .toString();
    }

    @Override
    public ModInfo getModInfo(String id) {
        return FabricLoader.getInstance()
                .getModContainer(id)
                .map(it -> new ModInfo(it.getMetadata().getName(), it.getMetadata().getVersion().toString()))
                .orElse(null);
    }
}
