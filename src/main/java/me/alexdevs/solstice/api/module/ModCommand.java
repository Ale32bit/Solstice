package me.alexdevs.solstice.api.module;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class ModCommand<T extends ModuleBase> {
    protected final T module;
    protected CommandDispatcher<CommandSourceStack> dispatcher;
    protected CommandBuildContext commandRegistry;
    protected Commands.CommandSelection environment;

    public ModCommand(T module) {
        this.commandRegistry = null;
        this.environment = null;
        this.dispatcher = null;

        this.module = module;
    }

    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandRegistry, Commands.CommandSelection environment) {
        this.dispatcher = dispatcher;
        this.commandRegistry = commandRegistry;
        this.environment = environment;

        var aliases = new ArrayList<>(getNames());
        var name = aliases.remove(0);
        var node = registerCommand(command(name));

        for (var alias : aliases) {
            dispatcher.register(Commands.literal(alias)
                    .requires(node.getRequirement())
                    .executes(node.getCommand())
                    .redirect(node));
        }
    }

    public LiteralCommandNode<CommandSourceStack> registerCommand(LiteralArgumentBuilder<CommandSourceStack> command) {
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

    public Predicate<CommandSourceStack> require() {
        return Permissions.require(getPermissionNode());
    }

    public Predicate<CommandSourceStack> require(int defaultRequiredLevel) {
        return Permissions.require(getPermissionNode(), defaultRequiredLevel);
    }

    public Predicate<CommandSourceStack> require(boolean defaultValue) {
        return Permissions.require(getPermissionNode(), defaultValue);
    }

    public Predicate<CommandSourceStack> require(String subNode) {
        return Permissions.require(getPermissionNode(subNode));
    }

    public Predicate<CommandSourceStack> require(String subNode, int defaultRequiredLevel) {
        return Permissions.require(getPermissionNode(subNode), defaultRequiredLevel);
    }

    public Predicate<CommandSourceStack> require(String subNode, boolean defaultValue) {
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
    public abstract LiteralArgumentBuilder<CommandSourceStack> command(String name);
}
