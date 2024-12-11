package me.alexdevs.solstice.modules.utilities.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.module.ModCommand;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class EnderchestCommand extends ModCommand {
    public EnderchestCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("enderchest");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(2))
                .executes(context -> execute(context, null))
                .then(argument("player", EntityArgumentType.player())
                        .requires(require("others", 2))
                        .executes(context -> execute(context, EntityArgumentType.getPlayer(context, "player"))));
    }

    private int execute(CommandContext<ServerCommandSource> context, @Nullable ServerPlayerEntity target) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.getPlayerOrThrow();

        if (target != null) {
            if (Permissions.check(target, getPermissionNode() + ".exempt")) {
                source.sendError(Text.of("You cannot open this Ender Chest because the player is exempt."));
                return 0;
            }
        } else {
            target = player;
        }

        var enderChestInventory = target.getEnderChestInventory();
        player.openHandledScreen(
                new SimpleNamedScreenHandlerFactory((syncId, inventory, playerx) ->
                        GenericContainerScreenHandler.createGeneric9x3(syncId, inventory, enderChestInventory),
                        Text.translatable("container.enderchest")));
        player.incrementStat(Stats.OPEN_ENDERCHEST);

        return 1;
    }
}
