package me.alexdevs.solstice.modules.skull.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.skull.SkullModule;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SkullCommand extends ModCommand<SkullModule> {
    public SkullCommand(SkullModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("skull");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(2))
                .executes(context -> execute(context, context.getSource().getPlayerOrThrow().getGameProfile().getName()))
                .then(argument("name", StringArgumentType.word())
                        .executes(context -> execute(context, StringArgumentType.getString(context, "name"))));
    }

    private int execute(CommandContext<ServerCommandSource> context, String skullName) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        var skull = module.createSkull(skullName);

        player.getInventory().insertStack(skull);
        return 1;
    }
}
