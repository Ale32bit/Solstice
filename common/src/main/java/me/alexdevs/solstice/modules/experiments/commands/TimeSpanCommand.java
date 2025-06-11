package me.alexdevs.solstice.modules.experiments.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.command.TimeSpan;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.experiments.ExperimentsModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class TimeSpanCommand extends ModCommand<ExperimentsModule> {

    public TimeSpanCommand(ExperimentsModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("timespan");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(true))
                .then(argument("timespan", StringArgumentType.string())
                        .suggests(TimeSpan::suggest)
                        .executes(this::execute));
    }

    private int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var timespan = TimeSpan.getTimeSpan(context, "timespan");
        context.getSource().sendSuccess(() -> Component.nullToEmpty(String.format("Got %s (%d)", TimeSpan.toShortString(timespan), timespan)), false);
        return 1;
    }
}
