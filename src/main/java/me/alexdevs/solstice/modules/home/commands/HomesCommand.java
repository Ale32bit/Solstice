package me.alexdevs.solstice.modules.home.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.home.HomeModule;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class HomesCommand extends ModCommand<HomeModule> {
    public HomesCommand(HomeModule module) {
        super(module);
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
        var data = module.getData(player.getUuid());
        var homeList = data.homes.keySet().stream().toList();
        var playerContext = PlaceholderContext.of(player);

        if (homeList.isEmpty()) {
            context.getSource().sendFeedback(() -> module.locale().get(
                    "noHomes",
                    playerContext
            ), false);
            return 1;
        }

        var listText = Text.empty();
        var comma = module.locale().get("homesComma");
        for (var i = 0; i < homeList.size(); i++) {
            if (i > 0) {
                listText = listText.append(comma);
            }
            var placeholders = Map.of(
                    "home", Text.of(homeList.get(i))
            );

            listText = listText.append(module.locale().get(
                    "homesFormat",
                    playerContext,
                    placeholders
            ));
        }

        var placeholders = Map.of(
                "homeList", (Text) listText
        );
        context.getSource().sendFeedback(() -> module.locale().get(
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
            context.getSource().sendFeedback(() -> module.locale().get(
                    "~tooManyTargets",
                    playerContext
            ), false);
            return 0;
        }

        var profile = profiles.iterator().next();

        var data = module.getData(profile.getId());
        var homeList = data.homes.keySet().stream().toList();

        if (homeList.isEmpty()) {
            var placeholders = Map.of(
                    "owner", Text.of(profile.getName())
            );
            context.getSource().sendFeedback(() -> module.locale().get(
                    "noHomesOther",
                    playerContext,
                    placeholders
            ), false);
            return 1;
        }

        var listText = Text.empty();
        var comma = module.locale().get("homesComma");
        for (var i = 0; i < homeList.size(); i++) {
            if (i > 0) {
                listText = listText.append(comma);
            }
            var placeholders = Map.of(
                    "home", Text.of(homeList.get(i)),
                    "owner", Text.of(profile.getName())
            );

            listText = listText.append(module.locale().get(
                    "homesFormatOther",
                    playerContext,
                    placeholders
            ));
        }

        var placeholders = Map.of(
                "homeList", listText,
                "owner", Text.of(profile.getName())
        );
        context.getSource().sendFeedback(() -> module.locale().get(
                "homeListOther",
                playerContext,
                placeholders
        ), false);

        return homeList.size();
    }
}