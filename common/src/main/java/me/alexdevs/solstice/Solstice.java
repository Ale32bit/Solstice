package me.alexdevs.solstice;

import me.alexdevs.solstice.api.data.HoconDataManager;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.events.WorldSaveCallback;
import me.alexdevs.solstice.api.events.proxy.ProxyServerLifecycleEvents;
import me.alexdevs.solstice.api.events.proxy.ProxyServerTickEvents;
import me.alexdevs.solstice.api.platform.PlatformHelper;
import me.alexdevs.solstice.core.*;
import me.alexdevs.solstice.data.PlayerDataManager;
import me.alexdevs.solstice.data.ServerData;
import me.alexdevs.solstice.integrations.ConnectorIntegration;
import me.alexdevs.solstice.integrations.LuckPermsIntegration;
import me.alexdevs.solstice.locale.LocaleManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.ConfigurateException;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Solstice {
    public static final String MOD_ID = "solstice";
    public static final Logger LOGGER = LoggerFactory.getLogger(Solstice.class);

    public static final HoconDataManager configManager = new HoconDataManager(Paths.configDirectory.resolve(
            "config.conf"));

    public static final LocaleManager localeManager = new LocaleManager(Paths.configDirectory.resolve("locale.json"));
    public static final ServerData serverData = new ServerData();
    public static final PlayerDataManager playerData = new PlayerDataManager();
    public static Modules modules = new Modules();
    private static final ConcurrentLinkedQueue<Runnable> nextTickRunnables = new ConcurrentLinkedQueue<>();
    public static MinecraftServer server;
    public static Scheduler scheduler = new Scheduler(1, nextTickRunnables);
    public static CooldownManager cooldown = new CooldownManager();
    public static final WarmUpManager warmUp = new WarmUpManager();
    private static Solstice INSTANCE;

    private static final UserCache userCache = new UserCache(PlatformHelper.get()
            .getGameDir()
            .resolve("usercache.json")
            .toFile());

    public void init() {
        INSTANCE = this;

        LOGGER.info("Initializing Solstice v{}...", PlatformHelper.get().getModVersion());

        PlatformHelper.get().init();
        ConnectorIntegration.register();
        LuckPermsIntegration.register();

        modules.register();
        modules.initModules();

        ToggleableConfig.get().save();

        try {
            configManager.prepareData();
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

        ProxyServerLifecycleEvents.SERVER_STARTING.register(server -> {
            Solstice.server = server;

            var path = server.getWorldPath(LevelResource.ROOT).resolve("data").resolve(MOD_ID);

            if (!path.toFile().exists()) {
                path.toFile().mkdirs();
            }

            serverData.setDataPath(path.resolve("server.json"));
            playerData.setDataPath(path.resolve("players"));
            serverData.loadData(false);
        });

        ProxyServerLifecycleEvents.SERVER_STARTED.register(server -> SolsticeEvents.READY.invoker()
                .onReady(INSTANCE, server));
        ProxyServerLifecycleEvents.SERVER_STOPPED.register(server -> scheduler.shutdownNow());

        WorldSaveCallback.EVENT.register((server1, suppressLogs, flush, force) -> {
            serverData.save();
            playerData.saveAll();
        });

        ProxyServerTickEvents.START_SERVER_TICK.register(server -> {
            nextTickRunnables.forEach(Runnable::run);
            nextTickRunnables.clear();
        });
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

    public void broadcast(Component text) {
        server.getPlayerList().broadcastSystemMessage(text, false);
    }
}