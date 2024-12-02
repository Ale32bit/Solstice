package me.alexdevs.solstice.commands.utilities;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;

import java.util.function.Predicate;

import static net.minecraft.server.command.CommandManager.literal;

public class WorkbenchCommand {
    private static final Predicate<ServerCommandSource> requirement = Permissions.require("solstice.command.workbench", 2);

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(command("workbench"));
        dispatcher.register(command("craft"));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> command(String command) {
        return literal(command)
                .requires(requirement)
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    var screen = new SimpleNamedScreenHandlerFactory(
                            (syncId, inventory, p) ->
                                    new CraftingScreenHandler(syncId, inventory, ScreenHandlerContext.EMPTY),
                            Text.translatable("container.crafting"));
                    player.openHandledScreen(screen);
                    player.incrementStat(Stats.INTERACT_WITH_CRAFTING_TABLE);

                    return 1;
                });
    }
}
