package me.alexdevs.solstice.commands.utilities;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class AnvilCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("anvil")
                .requires(Permissions.require("solstice.command.anvil", 2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    var screen = new SimpleNamedScreenHandlerFactory(
                            (syncId, inventory, p) ->
                                    new AnvilScreenHandler(syncId, inventory, ScreenHandlerContext.EMPTY),
                            Text.translatable("container.anvil"));
                    player.openHandledScreen(screen);
                    player.incrementStat(Stats.INTERACT_WITH_ANVIL);

                    return 1;
                });

        dispatcher.register(rootCommand);
    }
}
