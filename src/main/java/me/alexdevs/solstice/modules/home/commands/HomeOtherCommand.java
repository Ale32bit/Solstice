package me.alexdevs.solstice.modules.home.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.home.HomeModule;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class HomeOtherCommand extends ModCommand<HomeModule> {
    public HomeOtherCommand(HomeModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("homeother");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(2))
                .then(argument("player", GameProfileArgumentType.gameProfile())
                        .executes(context -> execute(context, "home"))
                        .then(argument("name", StringArgumentType.word())
                                .executes(context -> execute(context, StringArgumentType.getString(context, "name")))));
    }

    private int execute(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        var sourcePlayer = context.getSource().getPlayerOrThrow();
        var profiles = GameProfileArgumentType.getProfileArgument(context, "player");
        var playerContext = PlaceholderContext.of(context.getSource().getPlayer());

        if (profiles.size() > 1) {
            context.getSource().sendFeedback(() -> module.locale().get(
                    "~tooManyTargets",
                    playerContext
            ), false);
            return 0;
        }

        var profile = profiles.iterator().next();

        var data = module.getData(profile.getId());

        var placeholders = Map.of(
                "home", Text.of(name),
                "owner", Text.of(profile.getName())
        );

        if (!data.homes.containsKey(name)) {
            context.getSource().sendFeedback(() ->
                    module.locale().get(
                            "homeNotFound",
                            playerContext,
                            placeholders
                    ), false);

            return 1;
        }

        context.getSource().sendFeedback(() ->
                module.locale().get(
                        "teleportingOther",
                        playerContext,
                        placeholders
                ), true);

        var homePosition = data.homes.get(name);
        homePosition.teleport(sourcePlayer);

        return 1;
    }
}
