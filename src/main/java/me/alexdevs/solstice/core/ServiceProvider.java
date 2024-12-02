package me.alexdevs.solstice.core;

import com.mojang.brigadier.CommandDispatcher;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.IServiceProvider;
import me.alexdevs.solstice.api.events.ModuleRegistrationCallback;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.events.WorldSave;
import me.alexdevs.solstice.api.module.IModule;
import me.alexdevs.solstice.api.module.ModuleCommand;
import me.alexdevs.solstice.api.module.ModuleContainer;
import me.alexdevs.solstice.config.Config;
import me.alexdevs.solstice.config.ConfigManager;
import me.alexdevs.solstice.config.locale.Locale;
import me.alexdevs.solstice.config.locale.LocaleManager;
import me.alexdevs.solstice.coreLegacy.InfoPages;
import me.alexdevs.solstice.data.StateManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.WorldSavePath;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ServiceProvider implements IServiceProvider {
    public static final Path configDirectory = FabricLoader.getInstance().getConfigDir().resolve(Solstice.MOD_ID);
    public static final LocaleManager localeManager = new LocaleManager(configDirectory.resolve("locale.json"));
    public static final ConfigManager configManager = new ConfigManager(configDirectory.resolve("solstice.conf"));
    public static final StateManager state = new StateManager();
    public static MinecraftServer server;
    public static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private HashSet<ModuleContainer> containers = new HashSet<>();
    public final Solstice solstice;

    private CommandDispatcher<ServerCommandSource> dispatcher;
    private CommandRegistryAccess registry;
    private CommandManager.RegistrationEnvironment environment;

    public static Config config() {
        return configManager.config();
    }

    public static Locale locale() {
        return localeManager.locale();
    }

    public ServiceProvider(Solstice instance) {
        this.solstice = instance;
    }

    public void register() {
        ServerLifecycleEvents.SERVER_STARTING.register(s -> {
            server = s;
            state.register(server.getSavePath(WorldSavePath.ROOT).resolve("data").resolve(Solstice.MOD_ID));
            initContainers();
        });
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SolsticeEvents.READY.invoker().onReady(solstice, server);
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> scheduler.shutdown());
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> scheduler.shutdownNow());
        WorldSave.EVENT.register((server, suppressLogs, flush, force) -> state.save());

        CommandRegistrationCallback.EVENT.register((d, r, e) -> {
            dispatcher = d;
            registry = r;
            environment = e;
        });
    }

    private void initContainers() {
        containers = (HashSet<ModuleContainer>) ModuleRegistrationCallback.EVENT.invoker().register();

        for (ModuleContainer container : containers) {
            container.getModule().initialize(this);
        }

        registerCommands(dispatcher, registry, environment);

        for (ModuleContainer container : containers) {
            container.getModule().postInitialize(this);
        }

    }

    public Collection<ModuleContainer> getModules() {
        return Collections.unmodifiableCollection(containers);
    }

    public @Nullable ModuleContainer getModule(String id) {
        return containers.stream().filter(container -> container.getId().equals(id)).findFirst().orElse(null);
    }

    private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registry, CommandManager.RegistrationEnvironment environment) {
        var solCommand = CommandManager.literal("sol");
        for (ModuleContainer container : containers) {
            for (Class<? extends ModuleCommand> commandClass : container.getModule().getCommands()) {
                ModuleCommand command;
                try {
                    command = commandClass.getConstructor(IModule.class).newInstance(container.getModule());
                } catch (Exception e) {
                    Solstice.LOGGER.error("Could not instantiate a module command", e);
                    continue;
                }

                for (var name : command.getNames()) {
                    var literal = command.command(name);
                    dispatcher.register(literal);
                    solCommand.then(literal);
                }

                dispatcher.register(solCommand);
            }
        }
    }
}
