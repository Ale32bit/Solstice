package me.alexdevs.solstice.api.module;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.alexdevs.solstice.Solstice;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;
import java.util.function.Predicate;

public abstract class CommandProvider {
    protected final CommandContext<ServerCommandSource> context;
    protected final CommandRegistryAccess commandRegistry;
    protected final CommandManager.RegistrationEnvironment environment;
    protected final ModuleBase module;

    public CommandProvider(ModuleBase module, CommandContext<ServerCommandSource> context, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        this.module = module;
        this.context = context;
        this.commandRegistry = commandRegistry;
        this.environment = environment;
    }

    public String getName() {
        return getNames().stream().findFirst().orElseGet(() -> this.getClass().getSimpleName().toLowerCase());
    }

    public String getPermissionNode() {
        return Solstice.MOD_ID + ".command." + getName();
    }

    protected Predicate<ServerCommandSource> require() {
        return Permissions.require(getPermissionNode());
    }

    protected Predicate<ServerCommandSource> require(int defaultRequiredLevel) {
        return Permissions.require(getPermissionNode(), defaultRequiredLevel);
    }

    protected Predicate<ServerCommandSource> require(boolean defaultValue) {
        return Permissions.require(getPermissionNode(), defaultValue);
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
