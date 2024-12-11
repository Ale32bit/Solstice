package me.alexdevs.solstice.modules.home.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.locale.Locale;
import me.alexdevs.solstice.api.ServerPosition;
import me.alexdevs.solstice.util.Components;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SetHomeCommand extends ModCommand {
    private final Locale locale = Solstice.modules.home.getLocale();
    public SetHomeCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("sethome");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(true))
                .executes(context -> execute(context,
                        "home",
                        false))
                .then(argument("name", StringArgumentType.word())
                        .executes(context -> execute(context,
                                StringArgumentType.getString(context, "name"),
                                false))
                        .then(literal("force")
                                .executes(context -> execute(context,
                                        StringArgumentType.getString(context, "name"),
                                        true))));

    }

    private int execute(CommandContext<ServerCommandSource> context, String name, boolean forced) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        var data = Solstice.modules.home.getData(player.getUuid());
        var homes = data.homes;
        var playerContext = PlaceholderContext.of(player);

        var placeholders = Map.of(
                "home", Text.of(name),
                "forceSetButton", Components.button(
                        locale.raw("forceSetLabel"),
                        locale.raw("forceSetHover"),
                        "/sethome " + name + " true"
                )
        );

        var exists = homes.containsKey(name);
        if (exists && !forced) {
            var text = locale.get(
                    "homeExists",
                    playerContext,
                    placeholders
            );

            context.getSource().sendFeedback(() -> text, false);

            return 1;
        }


        var maxHomes = Solstice.modules.home.getConfig().maxHomes;
        if (maxHomes >= 0 && homes.size() >= maxHomes && !exists) {
            context.getSource().sendFeedback(() -> locale.get(
                    "maxHomesReached",
                    playerContext,
                    placeholders
            ), false);
            return 1;
        }

        var homePosition = new ServerPosition(player);
        homes.put(name, homePosition);

        context.getSource().sendFeedback(() -> locale.get(
                "homeSetSuccess",
                playerContext,
                placeholders
        ), false);

        return 1;
    }
}
