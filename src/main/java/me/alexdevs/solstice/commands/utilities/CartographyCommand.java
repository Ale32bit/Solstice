package me.alexdevs.solstice.commands.utilities;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.block.CartographyTableBlock;
import net.minecraft.screen.CartographyTableScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class CartographyCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("cartography")
                .requires(Permissions.require("solstice.command.cartography", 2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    var screen = new SimpleNamedScreenHandlerFactory(
                            (syncId, inventory, p) ->
                                    new CartographyTableScreenHandler(syncId, inventory, ScreenHandlerContext.EMPTY),
                            Text.translatable("container.cartography_table"));
                    player.openHandledScreen(screen);
                    player.incrementStat(Stats.INTERACT_WITH_CARTOGRAPHY_TABLE);

                    return 1;
                });

        dispatcher.register(rootCommand);
    }
}
