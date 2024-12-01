package me.alexdevs.solstice.commands.utilities;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.GiveCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class InventorySeeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("invsee")
                .requires(Permissions.require("solstice.command.invsee", 2))
                .then(argument("player", EntityArgumentType.player())
                        .executes(context -> {
                            /*
                            var player = context.getSource().getPlayerOrThrow();
                            var target = EntityArgumentType.getPlayer(context, "player");

                            var tInv = target.getInventory();

                            player.openHandledScreen(
                                    new SimpleNamedScreenHandlerFactory((syncId, inventory, playerx) ->
                                            new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X4, syncId, player.getInventory(), tInv.main, 4),
                                            target.getDisplayName()));

                             */
                            return 1;
                        }));

        dispatcher.register(rootCommand);
    }
}
