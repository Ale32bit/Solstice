package me.alexdevs.solstice.modules.utilities.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class SmithingCommand extends ModCommand {
    public SmithingCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("smithing");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(2))
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
    }
}
