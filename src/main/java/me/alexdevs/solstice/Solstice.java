package me.alexdevs.solstice;

import me.alexdevs.solstice.api.config.ConfigDataManager;
import me.alexdevs.solstice.api.config.IConfigDataManager;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.events.WorldSaveCallback;
import me.alexdevs.solstice.core.*;
import me.alexdevs.solstice.data.PlayerDataManager;
import me.alexdevs.solstice.data.ServerData;
import me.alexdevs.solstice.integrations.ConnectorIntegration;
import me.alexdevs.solstice.integrations.LuckPermsIntegration;
import me.alexdevs.solstice.locale.LocaleManager;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.ConfigurateException;

import java.util.concurrent.ConcurrentLinkedQueue;


public class Solstice implements DedicatedServerModInitializer {
    public static final String MOD_ID = "solstice";
    public static final Logger LOGGER = LoggerFactory.getLogger(Solstice.class);
    public static final SolsticeIdentifier ID = SolsticeIdentifier.of(MOD_ID, "");

    public static final IConfigDataManager configManager = new ConfigDataManager(Paths.configDirectory.resolve("config.conf"));
    public static final LocaleManager localeManager = new LocaleManager(Paths.configDirectory.resolve("locale.json"));
    public static final ServerData serverData = new ServerData();
    public static final PlayerDataManager playerData = new PlayerDataManager();
    public static final Modules modules = new Modules();
    private static final ConcurrentLinkedQueue<Runnable> nextTickRunnables = new ConcurrentLinkedQueue<>();
    public static MinecraftServer server;
    public static Scheduler scheduler = new Scheduler(1, nextTickRunnables);
    public static final WarmUpManager warmUp = new WarmUpManager();
    private static Solstice INSTANCE;
    private static final UserCache userCache = new UserCache(FabricLoader.getInstance().getGameDir().resolve("usercache.json").toFile());

    public Solstice() {
        INSTANCE = this;
    }

    public static Solstice getInstance() {
        return INSTANCE;
    }

    public static void nextTick(Runnable runnable) {
        nextTickRunnables.add(runnable);
    }

    public static UserCache getUserCache() {
        return userCache;
    }

    @Override
    public void onInitializeServer() {
        var modMeta = FabricLoader.getInstance().getModContainer(MOD_ID).get().getMetadata();
        LOGGER.info("Initializing Solstice v{}...", modMeta.getVersion());

        ConnectorIntegration.register();
        LuckPermsIntegration.register();

        modules.register();
        modules.initModules();

        ToggleableConfig.get().save();

        try {
            configManager.load();
            configManager.save();

        } catch (ConfigurateException e) {
            LOGGER.error("Error while loading Solstice config! Refusing to continue!", e);
            return;
        }

        try {
            localeManager.load();
            localeManager.save();
        } catch (Exception e) {
            LOGGER.error("Error while loading Solstice locale!", e);
        }

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            Solstice.server = server;
            var path = server.getWorldPath(LevelResource.ROOT).resolve("data").resolve(MOD_ID);
            if (!path.toFile().exists()) {
                path.toFile().mkdirs();
            }
            serverData.setDataPath(path.resolve("server.json"));
            playerData.setDataPath(path.resolve("players"));

            serverData.loadData(false);
        });
        ServerLifecycleEvents.SERVER_STARTED.register(server -> SolsticeEvents.READY.invoker().onReady(INSTANCE, server));
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> scheduler.shutdownNow());
        WorldSaveCallback.EVENT.register((server1, suppressLogs, flush, force) -> {
            serverData.save();
            playerData.saveAll();
        });

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            nextTickRunnables.forEach(Runnable::run);
            nextTickRunnables.clear();
        });
    }

    public void broadcast(Component text) {
        server.getPlayerList().broadcastSystemMessage(text, false);
    }
}