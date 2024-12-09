package me.alexdevs.solstice.modules.home.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.locale.Locale;
import me.alexdevs.solstice.util.Format;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class HomeOther extends ModCommand {
    private final Locale locale = Solstice.modules.home.getLocale();
    public HomeOther(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
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
            context.getSource().sendFeedback(() -> locale.get(
                    "~tooManyTargets",
                    playerContext
            ), false);
            return 0;
        }

        var profile = profiles.iterator().next();

        var data = Solstice.modules.home.getData(profile.getId());

        var placeholders = Map.of(
                "home", Text.of(name),
                "owner", Text.of(profile.getName())
        );

        if (!data.homes.containsKey(name)) {
            context.getSource().sendFeedback(() ->
                    locale.get(
                            "homeNotFound",
                            playerContext,
                            placeholders
                    ), false);

            return 1;
        }

        context.getSource().sendFeedback(() ->
                locale.get(
                        "teleportingOther",
                        playerContext,
                        placeholders
                ), true);

        var homePosition = data.homes.get(name);
        homePosition.teleport(sourcePlayer);

        return 1;
    }
}
