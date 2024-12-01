package me.alexdevs.solstice.commands.utilities;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class SmithingCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("smithing")
                .requires(Permissions.require("solstice.command.smithing", 2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    var screen = new SimpleNamedScreenHandlerFactory(
                            (syncId, inventory, p) ->
                                    new SmithingScreenHandler(syncId, inventory, ScreenHandlerContext.EMPTY),
                            Text.translatable("container.upgrade"));
                    player.openHandledScreen(screen);
                    player.incrementStat(Stats.INTERACT_WITH_SMITHING_TABLE);

                    return 1;
                });

        dispatcher.register(rootCommand);
    }
}
