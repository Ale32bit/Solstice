package me.alexdevs.solstice;

import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.events.WorldSave;
import me.alexdevs.solstice.data.PlayerDataManager;
import me.alexdevs.solstice.data.ServerData;
import me.alexdevs.solstice.locale.LocaleManager;
import me.alexdevs.solstice.modules.Modules;
import me.alexdevs.solstice.util.data.HoconDataManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.message.MessageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.ConfigurateException;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class Solstice implements ModInitializer {
    public static final String MOD_ID = "solstice";
    public static final Logger LOGGER = LoggerFactory.getLogger(Solstice.class);

    public static final Path configDirectory = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);

    public static final HoconDataManager configManager = new HoconDataManager(configDirectory.resolve("config.conf"));
    public static final LocaleManager localeManager = new LocaleManager(configDirectory.resolve("locale.json"));

    private static Solstice INSTANCE;

    public static Solstice getInstance() {
        return INSTANCE;
    }

    public static MinecraftServer server;

    public static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static final RegistryKey<MessageType> CHAT_TYPE = RegistryKey.of(RegistryKeys.MESSAGE_TYPE, new Identifier(MOD_ID, "chat"));

    public static final ServerData serverData = new ServerData();
    public static final PlayerDataManager playerData = new PlayerDataManager();

    public static final Modules modules = new Modules();

    private static final ConcurrentLinkedQueue<Runnable> nextTickRunnables = new ConcurrentLinkedQueue<>();

    public Solstice() {
        INSTANCE = this;
    }

    @Override
    public void onInitialize() {
        var modMeta = FabricLoader.getInstance().getModContainer(MOD_ID).get().getMetadata();
        LOGGER.info("Initializing Solstice v{}...", modMeta.getVersion());

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
            LOGGER.error("Error while loading Solstice locale! Refusing to continue!", e);
            return;
        }

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            Solstice.server = server;
            var path = server.getSavePath(WorldSavePath.ROOT).resolve("data").resolve(MOD_ID);
            if(!path.toFile().exists()) {
                path.toFile().mkdirs();
            }
            serverData.setDataPath(path.resolve("server.json"));
            playerData.setDataPath(path.resolve("players"));

            serverData.loadData(false);
        });
        ServerLifecycleEvents.SERVER_STARTED.register(server -> SolsticeEvents.READY.invoker().onReady(INSTANCE, server));
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> scheduler.shutdown());
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> scheduler.shutdownNow());
        WorldSave.EVENT.register((server1, suppressLogs, flush, force) -> {
            serverData.save();
            playerData.saveAll();
        });

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            nextTickRunnables.forEach(Runnable::run);
            nextTickRunnables.clear();
        });
    }

    public void broadcast(Text text) {
        server.getPlayerManager().broadcast(text, false);
    }

    public static void nextTick(Runnable runnable) {
        nextTickRunnables.add(runnable);
    }
}