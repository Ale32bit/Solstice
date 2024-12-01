package me.alexdevs.solstice.commands.utilities;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class TrashCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("trash")
                .requires(Permissions.require("solstice.command.trash", 2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();

                    player.openHandledScreen(
                            new SimpleNamedScreenHandlerFactory((syncId, inventory, playerx) ->
                                    GenericContainerScreenHandler.createGeneric9x3(syncId, inventory),
                                    Text.of("Trash")));

                    return 1;
                });

        dispatcher.register(rootCommand);
    }
}
