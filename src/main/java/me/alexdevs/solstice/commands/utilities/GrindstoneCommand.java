package me.alexdevs.solstice.commands.utilities;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.block.GrindstoneBlock;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class GrindstoneCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("grindstone")
                .requires(Permissions.require("solstice.command.grindstone", 2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    var screen = new SimpleNamedScreenHandlerFactory(
                            (syncId, inventory, p) ->
                                    new GrindstoneScreenHandler(syncId, inventory, ScreenHandlerContext.EMPTY),
                            Text.translatable("container.grindstone_title"));
                    player.openHandledScreen(screen);
                    player.incrementStat(Stats.INTERACT_WITH_GRINDSTONE);

                    return 1;
                });

        dispatcher.register(rootCommand);
    }
}
