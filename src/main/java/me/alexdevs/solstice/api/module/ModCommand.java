package me.alexdevs.solstice.api.module;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.alexdevs.solstice.Solstice;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;
import java.util.function.Predicate;

public abstract class ModCommand {
    protected final CommandDispatcher<ServerCommandSource> dispatcher;
    protected final CommandRegistryAccess commandRegistry;
    protected final CommandManager.RegistrationEnvironment environment;

    public ModCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        this.dispatcher = dispatcher;
        this.commandRegistry = commandRegistry;
        this.environment = environment;

        this.register();
    }

    public void register() {
        for (var name : getNames()) {
            dispatcher.register(command(name));
        }
    }

    public String getName() {
        return getNames().stream().findFirst().orElseGet(() -> this.getClass().getSimpleName().toLowerCase());
    }

    public String getPermissionNode() {
        return Solstice.MOD_ID + ".command." + getName();
    }

    public Predicate<ServerCommandSource> require() {
        return Permissions.require(getPermissionNode());
    }

    public Predicate<ServerCommandSource> require(int defaultRequiredLevel) {
        return Permissions.require(getPermissionNode(), defaultRequiredLevel);
    }

    public Predicate<ServerCommandSource> require(boolean defaultValue) {
        return Permissions.require(getPermissionNode(), defaultValue);
    }

    public Predicate<ServerCommandSource> require(String subNode) {
        return Permissions.require(getPermissionNode() + "." + subNode);
    }

    public Predicate<ServerCommandSource> require(String subNode, int defaultRequiredLevel) {
        return Permissions.require(getPermissionNode() + "." + subNode, defaultRequiredLevel);
    }

    public Predicate<ServerCommandSource> require(String subNode, boolean defaultValue) {
        return Permissions.require(getPermissionNode() + "." + subNode, defaultValue);
    }

    /**
     * Define the name and aliases of the command. First value is the name, next values are aliases.
     *
     * @return List of names
     */
    public abstract List<String> getNames();

    /**
     * Generate the command node, this method gets called for every name.
     *
     * @param name Command name
     * @return Command node
     */
    public abstract LiteralArgumentBuilder<ServerCommandSource> command(String name);
}
