package me.alexdevs.solstice.commands.utilities;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.block.LoomBlock;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.LoomScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class LoomCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("loom")
                .requires(Permissions.require("solstice.command.loom", 2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    var screen = new SimpleNamedScreenHandlerFactory(
                            (syncId, inventory, p) ->
                                    new LoomScreenHandler(syncId, inventory, ScreenHandlerContext.EMPTY),
                            Text.translatable("container.loom"));
                    player.openHandledScreen(screen);
                    player.incrementStat(Stats.INTERACT_WITH_LOOM);
                    return 1;
                });

        dispatcher.register(rootCommand);
    }
}
