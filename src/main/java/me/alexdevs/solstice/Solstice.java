package me.alexdevs.solstice;

import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.events.WorldSave;
import me.alexdevs.solstice.commands.CommandInitializer;
import me.alexdevs.solstice.config.Config;
import me.alexdevs.solstice.config.ConfigManager;
import me.alexdevs.solstice.config.locale.Locale;
import me.alexdevs.solstice.config.locale.LocaleManager;
import me.alexdevs.solstice.core.*;
import me.alexdevs.solstice.core.customFormats.CustomChatMessage;
import me.alexdevs.solstice.data.StateManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.ConfigurateException;

import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class Solstice implements ModInitializer {
    public static final String MOD_ID = "solstice";
    public static final Logger LOGGER = LoggerFactory.getLogger(Solstice.class);

    private static final Path configDirectory = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
    public static final ConfigManager configManager = new ConfigManager(configDirectory.resolve("solstice.conf"));
    public static final LocaleManager localeManager = new LocaleManager(configDirectory.resolve("locale.json"));
    public static Config config() {
        return configManager.config();
    }
    public static Locale locale() {
        return localeManager.locale();
    }

    public static final StateManager state = new StateManager();

    private static Solstice INSTANCE;

    public static Solstice getInstance() {
        return INSTANCE;
    }

    public static MinecraftServer server;

    public static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static final RegistryKey<MessageType> CHAT_TYPE = RegistryKey.of(RegistryKeys.MESSAGE_TYPE, new Identifier(MOD_ID, "chat"));
    private static boolean warnedAboutUnsignedMessages = false;

    public Solstice() {
        INSTANCE = this;
    }

    @Override
    public void onInitialize() {
        var modMeta = FabricLoader.getInstance().getModContainer(MOD_ID).get().getMetadata();
        LOGGER.info("Initializing Solstice v{}...", modMeta.getVersion());

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
            LOGGER.error("Error while loading Solstice locale! Refusing to continue!", e);
            return;
        }

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            Solstice.server = server;
            state.register(server.getSavePath(WorldSavePath.ROOT).resolve("data").resolve(MOD_ID));
        });
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SolsticeEvents.READY.invoker().onReady(INSTANCE, server);
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> scheduler.shutdown());
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            scheduler.shutdownNow();
        });
        WorldSave.EVENT.register((server1, suppressLogs, flush, force) -> state.save());

        CommandInitializer.register();
        AfkTracker.register();
        TeleportTracker.register();
        BackTracker.register();
        TabList.register();
        BossBarManager.register();
        AutoRestart.register();
        MailManager.register();
        CommandSpy.register();
        AutoAnnouncements.register();
        Motd.register();
        MuteManager.register();
    }

    public void broadcast(Text text) {
        server.getPlayerManager().broadcast(text, false);
    }

    public void sendChatAsPlayer(ServerPlayerEntity player, String message) {
        var msgType = server.getRegistryManager().get(RegistryKeys.MESSAGE_TYPE).getOrThrow(MessageType.CHAT);
        var signedMessage = SignedMessage.ofUnsigned(player.getUuid(), message);
        var pars = new MessageType.Parameters(msgType, Text.of(message), Text.of(message));

        var allowed = ServerMessageEvents.ALLOW_CHAT_MESSAGE.invoker().allowChatMessage(signedMessage, player, pars);
        if (!allowed)
            return;

        ServerMessageEvents.CHAT_MESSAGE.invoker().onChatMessage(signedMessage, player, pars);

        var formatted = CustomChatMessage.getFormattedMessage(signedMessage, player);
        for (var pl : server.getPlayerManager().getPlayerList()) {
            pl.sendMessage(formatted);
        }
    }

    public static void warnUnsignedMessages() {
        if (warnedAboutUnsignedMessages)
            return;
        warnedAboutUnsignedMessages = true;

        LOGGER.warn(
                """
                        !!! --- WARNING --- !!!
                        Cannot retrieve message sender UUID!
                        
                        If you are using FabricProxy-Lite, consider disabling
                        the `hackMessageChain` configuration!
                        """
        );
    }

}