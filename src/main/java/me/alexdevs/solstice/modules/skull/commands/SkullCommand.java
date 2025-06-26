package me.alexdevs.solstice.modules.skull.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.skull.SkullModule;
import net.minecraft.commands.CommandSourceStack;

import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class SkullCommand extends ModCommand<SkullModule> {
    public SkullCommand(SkullModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("skull");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(2))
                .executes(context -> execute(context, context.getSource().getPlayerOrException().getGameProfile().getName()))
                .then(argument("name", StringArgumentType.word())
                        .executes(context -> execute(context, StringArgumentType.getString(context, "name"))));
    }

    private int execute(CommandContext<CommandSourceStack> context, String skullName) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        var skull = module.createSkull(skullName);

        player.getInventory().add(skull);
        return 1;
    }
}
