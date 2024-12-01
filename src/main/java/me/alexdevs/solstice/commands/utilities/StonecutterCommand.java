package me.alexdevs.solstice.commands.utilities;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class StonecutterCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("stonecutter")
                .requires(Permissions.require("solstice.command.stonecutter", 2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    var screen = new SimpleNamedScreenHandlerFactory(
                            (syncId, inventory, p) ->
                                    new StonecutterScreenHandler(syncId, inventory, ScreenHandlerContext.EMPTY),
                            Text.translatable("container.stonecutter"));
                    player.openHandledScreen(screen);
                    player.incrementStat(Stats.INTERACT_WITH_STONECUTTER);

                    return 1;
                });

        dispatcher.register(rootCommand);
    }
}
