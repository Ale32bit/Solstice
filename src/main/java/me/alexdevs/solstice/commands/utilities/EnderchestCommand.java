package me.alexdevs.solstice.commands.utilities;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class EnderchestCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("enderchest")
                .requires(Permissions.require("solstice.command.enderchest", 2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();

                    var enderChestInventory = player.getEnderChestInventory();
                    player.openHandledScreen(
                            new SimpleNamedScreenHandlerFactory((syncId, inventory, playerx) ->
                                    GenericContainerScreenHandler.createGeneric9x3(syncId, inventory, enderChestInventory),
                                    Text.translatable("container.enderchest")));
                    player.incrementStat(Stats.OPEN_ENDERCHEST);

                    return 1;
                });

        dispatcher.register(rootCommand);
    }
}
