package me.alexdevs.solstice.modules.home.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.locale.Locale;
import me.alexdevs.solstice.util.Format;
import com.mojang.brigadier.CommandDispatcher;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class HomesCommand extends ModCommand {
    private final Locale locale = Solstice.modules.home.getLocale();

    public HomesCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("homes");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(true))
                .executes(this::execute)
                .then(argument("player", GameProfileArgumentType.gameProfile())
                        .requires(require("others", 2))
                        .executes(context -> executeOthers(context, GameProfileArgumentType.getProfileArgument(context, "player"))));
    }

    private int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        var data = Solstice.modules.home.getData(player.getUuid());
        var homeList = data.homes.keySet().stream().toList();
        var playerContext = PlaceholderContext.of(player);

        if (homeList.isEmpty()) {
            context.getSource().sendFeedback(() -> locale.get(
                    "noHomes",
                    playerContext
            ), false);
            return 1;
        }

        var listText = Text.empty();
        var comma = locale.get("homesComma");
        for (var i = 0; i < homeList.size(); i++) {
            if (i > 0) {
                listText = listText.append(comma);
            }
            var placeholders = Map.of(
                    "home", Text.of(homeList.get(i))
            );

            listText = listText.append(locale.get(
                    "homesFormat",
                    playerContext,
                    placeholders
            ));
        }

        var placeholders = Map.of(
                "homeList", (Text) listText
        );
        context.getSource().sendFeedback(() -> locale.get(
                "homeList",
                playerContext,
                placeholders
        ), false);

        return homeList.size();
    }

    private int executeOthers(CommandContext<ServerCommandSource> context, Collection<GameProfile> profiles) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        var playerContext = PlaceholderContext.of(player);

        if (profiles.size() > 1) {
            context.getSource().sendFeedback(() -> locale.get(
                    "~tooManyTargets",
                    playerContext
            ), false);
            return 0;
        }

        var profile = profiles.iterator().next();

        var data = Solstice.modules.home.getData(profile.getId());
        var homeList = data.homes.keySet().stream().toList();

        if (homeList.isEmpty()) {
            var placeholders = Map.of(
                    "owner", Text.of(profile.getName())
            );
            context.getSource().sendFeedback(() -> locale.get(
                    "noHomesOther",
                    playerContext,
                    placeholders
            ), false);
            return 1;
        }

        var listText = Text.empty();
        var comma = locale.get("homesComma");
        for (var i = 0; i < homeList.size(); i++) {
            if (i > 0) {
                listText = listText.append(comma);
            }
            var placeholders = Map.of(
                    "home", Text.of(homeList.get(i)),
                    "owner", Text.of(profile.getName())
            );

            listText = listText.append(locale.get(
                    "homesFormatOther",
                    playerContext,
                    placeholders
            ));
        }

        var placeholders = Map.of(
                "homeList", listText,
                "owner", Text.of(profile.getName())
        );
        context.getSource().sendFeedback(() -> locale.get(
                "homeListOther",
                playerContext,
                placeholders
        ), false);

        return homeList.size();
    }
}