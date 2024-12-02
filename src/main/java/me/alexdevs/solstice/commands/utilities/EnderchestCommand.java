package me.alexdevs.solstice.commands.utilities;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class EnderchestCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("enderchest")
                .requires(Permissions.require("solstice.command.enderchest", 2))
                .executes(context -> execute(context, null))
                .then(argument("player", EntityArgumentType.player())
                        .requires(Permissions.require("solstice.command.enderchest.others", 3))
                        .executes(context -> execute(context, EntityArgumentType.getPlayer(context, "player"))));

        dispatcher.register(rootCommand);
    }

    private static int execute(CommandContext<ServerCommandSource> context, @Nullable ServerPlayerEntity target) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.getPlayerOrThrow();

        if (target != null) {
            if (Permissions.check(target, "solstice.command.enderchest.exempt")) {
                source.sendError(Text.of("You cannot open this enderchest because the player is exempt."));
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
