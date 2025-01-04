package me.alexdevs.solstice.api.module;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;
import java.util.function.Predicate;

public abstract class ModCommand<T extends ModuleBase> {
    protected final T module;
    protected CommandDispatcher<ServerCommandSource> dispatcher;
    protected CommandRegistryAccess commandRegistry;
    protected CommandManager.RegistrationEnvironment environment;

    public ModCommand(T module) {
        this.commandRegistry = null;
        this.environment = null;
        this.dispatcher = null;

        this.module = module;
    }

    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        this.dispatcher = dispatcher;
        this.commandRegistry = commandRegistry;
        this.environment = environment;

        for (var name : getNames()) {
            registerCommand(command(name));
        }
    }

    public LiteralCommandNode<ServerCommandSource> registerCommand(LiteralArgumentBuilder<ServerCommandSource> command) {
        return dispatcher.register(command);
    }

    public String getName() {
        return getNames().stream().findFirst().orElseGet(() -> this.getClass().getSimpleName().toLowerCase());
    }

    public String getPermissionNode() {
        var node = module.getPermissionNode("base");
        Debug.commandDebugList.add(new Debug.CommandDebug(module.id, getName(), getNames(), node));
        return node;
    }

    public String getPermissionNode(String subNode) {
        var node = module.getPermissionNode(subNode);
        Debug.commandDebugList.add(new Debug.CommandDebug(module.id, getName(), getNames(), node));
        return node;
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
        return Permissions.require(getPermissionNode(subNode));
    }

    public Predicate<ServerCommandSource> require(String subNode, int defaultRequiredLevel) {
        return Permissions.require(getPermissionNode(subNode), defaultRequiredLevel);
    }

    public Predicate<ServerCommandSource> require(String subNode, boolean defaultValue) {
        return Permissions.require(getPermissionNode(subNode), defaultValue);
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
