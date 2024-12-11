package me.alexdevs.solstice.modules.utilities.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Predicate;

import static net.minecraft.server.command.CommandManager.literal;

public class WorkbenchCommand extends ModCommand {
    public WorkbenchCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("workbench", "craft");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(2))
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
