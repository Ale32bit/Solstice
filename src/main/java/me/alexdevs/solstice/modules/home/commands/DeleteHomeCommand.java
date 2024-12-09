package me.alexdevs.solstice.modules.home.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.locale.Locale;
import me.alexdevs.solstice.Solstice;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DeleteHomeCommand extends ModCommand {
    private final Locale locale = Solstice.modules.home.getLocale();
    public DeleteHomeCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("delhome");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(true))
                .executes(context -> execute(context, "home"))
                .then(argument("name", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            if (!context.getSource().isExecutedByPlayer())
                                return CommandSource.suggestMatching(new String[]{}, builder);

                            var data = Solstice.modules.home.getData(context.getSource().getPlayer().getUuid());

                            return CommandSource.suggestMatching(data.homes.keySet().stream(), builder);
                        })
                        .executes(context -> execute(context, StringArgumentType.getString(context, "name"))));
    }

    private int execute(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        var data = Solstice.modules.home.getData(player.getUuid());
        var playerContext = PlaceholderContext.of(player);

        var placeholders = Map.of(
                "home", Text.of(name)
        );

        if (!data.homes.containsKey(name)) {
            context.getSource().sendFeedback(() -> locale.get(
                    "homeNotFound",
                    playerContext,
                    placeholders
            ), false);
            return 1;
        }

        data.homes.remove(name);

        context.getSource().sendFeedback(() -> locale.get(
                "homeDeleted",
                playerContext,
                placeholders
        ), false);

        return 1;
    }
}
