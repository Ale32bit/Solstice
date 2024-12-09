package me.alexdevs.solstice.modules.autorestart.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RestartCommand extends ModCommand {
    public RestartCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("restart");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(4))
                .then(literal("schedule")
                        .then(argument("seconds", IntegerArgumentType.integer(0))
                                .executes(context -> schedule(context, IntegerArgumentType.getInteger(context, "seconds"), null))
                                .then(argument("message", StringArgumentType.greedyString())
                                        .executes(context -> schedule(context, IntegerArgumentType.getInteger(context, "seconds"), StringArgumentType.getString(context, "message")))))
                        .then(literal("next")
                                .executes(this::scheduleNext))
                )
                .then(literal("cancel")
                        .executes(this::cancel));
    }

    private int schedule(CommandContext<ServerCommandSource> context, int seconds, @Nullable String message) {
        if (message == null) {
            message = Solstice.config().autoRestart.restartBarLabel;
        }
        Solstice.modules.autoRestart.schedule(seconds, message);

        context.getSource().sendFeedback(() -> Text.of("Manual restart scheduled in " + seconds + " seconds."), true);

        return 1;
    }

    private int scheduleNext(CommandContext<ServerCommandSource> context) {
        if (Solstice.modules.autoRestart.isScheduled()) {
            context.getSource().sendFeedback(() -> Text.literal("There is already a scheduled restart.").formatted(Formatting.RED), false);
            return 1;
        }

        var delay = Solstice.modules.autoRestart.scheduleNextRestart();

        if (delay == null) {
            context.getSource().sendFeedback(() -> Text.literal("Could not schedule next automatic restart.").formatted(Formatting.RED), false);
        } else {
            context.getSource().sendFeedback(() -> Text.literal("Next automatic restart scheduled in " + delay + " seconds."), true);
        }

        return 1;
    }

    private int cancel(CommandContext<ServerCommandSource> context) {
        if (!Solstice.modules.autoRestart.isScheduled()) {
            context.getSource().sendFeedback(() -> Text.literal("There is no scheduled restart.").formatted(Formatting.RED), false);
            return 1;
        }

        Solstice.modules.autoRestart.cancel();
        context.getSource().sendFeedback(() -> Text.literal("Restart schedule canceled."), true);
        return 1;
    }
}
