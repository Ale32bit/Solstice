package me.alexdevs.solstice.modules.restart.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.command.TimeSpan;
import me.alexdevs.solstice.api.events.RestartEvents;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.restart.RestartModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class RestartCommand extends ModCommand<RestartModule> {
    public RestartCommand(RestartModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("restart");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(4))
                .then(literal("now")
                        .executes(context -> {
                            context.getSource().sendSuccess(() -> Component.nullToEmpty("Restarting server"), true);
                            module.restart();
                            return 1;
                        }))
                .then(literal("schedule")
                        .then(argument("timespan", StringArgumentType.word())
                                .suggests(TimeSpan::suggest)
                                .executes(context -> schedule(context, TimeSpan.getTimeSpan(context, "timespan"), null))
                                .then(argument("message", StringArgumentType.greedyString())
                                        .executes(context -> schedule(context, TimeSpan.getTimeSpan(context, "timespan"), StringArgumentType.getString(context, "message")))))
                        .then(literal("next")
                                .executes(this::scheduleNext))
                )
                .then(literal("cancel")
                        .executes(this::cancel));
    }

    private int schedule(CommandContext<CommandSourceStack> context, int seconds, @Nullable String message) {
        if (module.isRunning()) {
            context.getSource().sendFailure(Component.nullToEmpty("There is already a running restart."));
            return 0;
        }

        if (message == null) {
            message = Solstice.localeManager.getLocale(RestartModule.ID).raw("barLabel");
        }
        module.schedule(seconds, message, RestartEvents.RestartType.MANUAL);

        context.getSource().sendSuccess(() -> Component.nullToEmpty("Manual restart scheduled in " + seconds + " seconds."), true);

        return 1;
    }

    private int scheduleNext(CommandContext<CommandSourceStack> context) {
        if (module.isScheduled()) {
            context.getSource().sendFailure(Component.nullToEmpty("There is already a scheduled restart."));
            return 0;
        }

        var delay = module.scheduleNextRestart();

        if (delay == null) {
            context.getSource().sendFailure(Component.nullToEmpty("Could not schedule next automatic restart."));
            return 0;
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Next automatic restart scheduled in " + delay + " seconds."), true);
        }

        return 1;
    }

    private int cancel(CommandContext<CommandSourceStack> context) {
        if (!module.isScheduled()) {
            context.getSource().sendFailure(Component.nullToEmpty("There is no scheduled restart."));
            return 0;
        }

        module.cancel();
        context.getSource().sendSuccess(() -> Component.literal("Restart schedule canceled."), true);
        return 1;
    }
}
