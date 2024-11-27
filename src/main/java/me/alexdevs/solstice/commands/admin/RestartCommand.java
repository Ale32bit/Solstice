package me.alexdevs.solstice.commands.admin;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.core.AutoRestart;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RestartCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("restart")
                .requires(Permissions.require("solstice.command.restart", 4))
                .then(literal("schedule")
                        .then(argument("seconds", IntegerArgumentType.integer(0))
                                .executes(context -> schedule(context, IntegerArgumentType.getInteger(context, "seconds"), null))
                                .then(argument("message", StringArgumentType.greedyString())
                                        .executes(context -> schedule(context, IntegerArgumentType.getInteger(context, "seconds"), StringArgumentType.getString(context, "message")))))
                        .then(literal("next")
                                .executes(RestartCommand::scheduleNext))
                )
                .then(literal("cancel")
                        .executes(RestartCommand::cancel));

        dispatcher.register(rootCommand);
    }

    private static int schedule(CommandContext<ServerCommandSource> context, int seconds, @Nullable String message) {
        if (message == null) {
            message = Solstice.config().autoRestart.restartBarLabel;
        }
        AutoRestart.schedule(seconds, message);

        context.getSource().sendFeedback(() -> Text.of("Manual restart scheduled in " + seconds + " seconds."), true);

        return 1;
    }

    private static int scheduleNext(CommandContext<ServerCommandSource> context) {
        if (AutoRestart.isScheduled()) {
            context.getSource().sendFeedback(() -> Text.literal("There is already a scheduled restart.").formatted(Formatting.RED), false);
            return 1;
        }

        var delay = AutoRestart.scheduleNextRestart();

        if (delay == null) {
            context.getSource().sendFeedback(() -> Text.literal("Could not schedule next automatic restart.").formatted(Formatting.RED), false);
        } else {
            context.getSource().sendFeedback(() -> Text.literal("Next automatic restart scheduled in " + delay + " seconds."), true);
        }

        return 1;
    }

    private static int cancel(CommandContext<ServerCommandSource> context) {
        if (!AutoRestart.isScheduled()) {
            context.getSource().sendFeedback(() -> Text.literal("There is no scheduled restart.").formatted(Formatting.RED), false);
            return 1;
        }

        AutoRestart.cancel();
        context.getSource().sendFeedback(() -> Text.literal("Restart schedule canceled."), true);
        return 1;
    }
}
