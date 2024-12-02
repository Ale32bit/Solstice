package me.alexdevs.solstice;

import me.alexdevs.solstice.core.ServiceProvider;
import me.alexdevs.solstice.modules.ModuleProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.message.MessageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.ConfigurateException;


public class Solstice implements ModInitializer {
    public static final String MOD_ID = "solstice";
    public static final Logger LOGGER = LoggerFactory.getLogger(Solstice.class);

    private static Solstice INSTANCE;
    public static Solstice getInstance() {
        return INSTANCE;
    }

    public static final RegistryKey<MessageType> CHAT_TYPE = RegistryKey.of(RegistryKeys.MESSAGE_TYPE, new Identifier(MOD_ID, "chat"));

    public final ServiceProvider serviceProvider;

    public Solstice() {
        INSTANCE = this;
        serviceProvider = new ServiceProvider(INSTANCE);
    }

    @Override
    public void onInitialize() {
        var modMeta = FabricLoader.getInstance().getModContainer(MOD_ID).get().getMetadata();
        LOGGER.info("Initializing Solstice v{}...", modMeta.getVersion());

        serviceProvider.register();

        try {
            ServiceProvider.configManager.load();
            ServiceProvider.configManager.save();
        } catch (ConfigurateException e) {
            LOGGER.error("Error while loading Solstice config! Refusing to continue!", e);
            return;
        }

        try {
            ServiceProvider.localeManager.load();
            ServiceProvider.localeManager.save();
        } catch (Exception e) {
            LOGGER.error("Error while loading Solstice locale! Refusing to continue!", e);
            return;
        }

        ModuleProvider.initialize();

        /*CommandInitializer.register();
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
        MuteManager.register();*/
    }


    public void broadcast(Text text) {
        ServiceProvider.server.getPlayerManager().broadcast(text, false);
    }

}