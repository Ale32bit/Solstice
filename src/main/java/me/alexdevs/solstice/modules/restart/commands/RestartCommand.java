package me.alexdevs.solstice.modules.restart.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.command.TimeSpan;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.restart.RestartModule;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RestartCommand extends ModCommand<RestartModule> {
    public RestartCommand(RestartModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("restart");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(4))
                .then(literal("now")
                        .executes(context -> {
                            context.getSource().sendFeedback(() -> Text.of("Restarting server"), true);
                            module.restart();
                            return 1;
                        }))
                .then(literal("schedule")
                        .then(argument("timespan", StringArgumentType.word())
                                .suggests(TimeSpan::suggest)
                                .executes(context -> schedule(context, TimeSpan.getTimeSpan(context, "timespan"), null))
                                .then(argument("message", StringArgumentType.greedyString())
                                        .executes(context -> schedule(context, IntegerArgumentType.getInteger(context, "seconds"), StringArgumentType.getString(context, "message")))))
                        .then(literal("next")
                                .executes(this::scheduleNext))
                )
                .then(literal("cancel")
                        .executes(this::cancel));
    }

    private int schedule(CommandContext<ServerCommandSource> context, int seconds, @Nullable String message) {
        if (module.isRunning()) {
            context.getSource().sendError(Text.of("There is already a running restart."));
            return 0;
        }

        if (message == null) {
            message = Solstice.localeManager.getLocale(RestartModule.ID).raw("barLabel");
        }
        module.schedule(seconds, message);

        context.getSource().sendFeedback(() -> Text.of("Manual restart scheduled in " + seconds + " seconds."), true);

        return 1;
    }

    private int scheduleNext(CommandContext<ServerCommandSource> context) {
        if (module.isScheduled()) {
            context.getSource().sendError(Text.of("There is already a scheduled restart."));
            return 0;
        }

        var delay = module.scheduleNextRestart();

        if (delay == null) {
            context.getSource().sendError(Text.of("Could not schedule next automatic restart."));
            return 0;
        } else {
            context.getSource().sendFeedback(() -> Text.literal("Next automatic restart scheduled in " + delay + " seconds."), true);
        }

        return 1;
    }

    private int cancel(CommandContext<ServerCommandSource> context) {
        if (!module.isScheduled()) {
            context.getSource().sendError(Text.of("There is no scheduled restart."));
            return 0;
        }

        module.cancel();
        context.getSource().sendFeedback(() -> Text.literal("Restart schedule canceled."), true);
        return 1;
    }
}
