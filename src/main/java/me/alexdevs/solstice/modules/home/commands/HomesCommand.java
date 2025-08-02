package me.alexdevs.solstice.modules.home.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.command.LocalGameProfile;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.home.HomeModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class HomesCommand extends ModCommand<HomeModule> {
    public HomesCommand(HomeModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("homes");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(true))
                .executes(this::execute)
                .then(argument("player", StringArgumentType.word())
                        .requires(require("others", 2))
                        .suggests(LocalGameProfile::suggest)
                        .executes(context -> executeOthers(context, LocalGameProfile.getProfile(context, "player"))));
    }

    private int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        var data = module.getData(player.getUUID());
        var homeList = data.homes.keySet().stream().sorted().toList();
        var playerContext = PlaceholderContext.of(player);

        if (homeList.isEmpty()) {
            context.getSource().sendSuccess(() -> module.locale().get(
                    "noHomes",
                    playerContext
            ), false);
            return 1;
        }

        var listText = Component.empty();
        var comma = module.locale().get("homesComma");
        for (var i = 0; i < homeList.size(); i++) {
            if (i > 0) {
                listText = listText.append(comma);
            }
            var placeholders = Map.of(
                    "home", Component.nullToEmpty(homeList.get(i))
            );

            listText = listText.append(module.locale().get(
                    "homesFormat",
                    playerContext,
                    placeholders
            ));
        }

        var placeholders = Map.of(
                "homeList", (Component) listText
        );
        context.getSource().sendSuccess(() -> module.locale().get(
                "homeList",
                playerContext,
                placeholders
        ), false);

        return homeList.size();
    }

    private int executeOthers(CommandContext<CommandSourceStack> context, GameProfile profile) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        var playerContext = PlaceholderContext.of(player);

        var data = module.getData(profile.getId());
        var homeList = data.homes.keySet().stream().sorted().toList();

        if (homeList.isEmpty()) {
            var placeholders = Map.of(
                    "owner", Component.nullToEmpty(profile.getName())
            );
            context.getSource().sendSuccess(() -> module.locale().get(
                    "noHomesOther",
                    playerContext,
                    placeholders
            ), false);
            return 1;
        }

        var listText = Component.empty();
        var comma = module.locale().get("homesComma");
        for (var i = 0; i < homeList.size(); i++) {
            if (i > 0) {
                listText = listText.append(comma);
            }
            var placeholders = Map.of(
                    "home", Component.nullToEmpty(homeList.get(i)),
                    "owner", Component.nullToEmpty(profile.getName())
            );

            listText = listText.append(module.locale().get(
                    "homesFormatOther",
                    playerContext,
                    placeholders
            ));
        }

        var placeholders = Map.of(
                "homeList", listText,
                "owner", Component.nullToEmpty(profile.getName())
        );
        context.getSource().sendSuccess(() -> module.locale().get(
                "homeListOther",
                playerContext,
                placeholders
        ), false);

        return homeList.size();
    }
}