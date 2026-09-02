package me.alexdevs.solstice.modules.experiments.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.command.TimeSpan;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.text.Components;
import me.alexdevs.solstice.modules.experiments.ExperimentsModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ButtonCommand extends ModCommand<ExperimentsModule> {

    public ButtonCommand(ExperimentsModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("button");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(true))
                .then(literal("ok").executes(context -> {
                    context.getSource().sendSuccess(() -> Component.literal("Button pressed"), false);
                    return 1;
                }))
                .executes(this::execute);
    }

    private int execute(CommandContext<CommandSourceStack> context) {


        context.getSource().sendSuccess(() -> Components.button("Test Button", "Click to test the button", "/button ok"), false);
        return 1;
    }
}
