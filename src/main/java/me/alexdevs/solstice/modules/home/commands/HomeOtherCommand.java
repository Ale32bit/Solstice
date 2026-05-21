package me.alexdevs.solstice.modules.home.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.command.LocalGameProfile;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.utils.PlayerUtils;
import me.alexdevs.solstice.modules.home.HomeModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class HomeOtherCommand extends ModCommand<HomeModule> {
    public HomeOtherCommand(HomeModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("homeother");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require("others", 2))
                .then(argument("player", StringArgumentType.word())
                        .suggests(LocalGameProfile::suggest)
                        .executes(context -> execute(context, "home"))
                        .then(argument("name", StringArgumentType.word())
                                .executes(context -> execute(context, StringArgumentType.getString(context, "name")))));
    }

    private int execute(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        var sourcePlayer = context.getSource().getPlayerOrException();
        var profile = LocalGameProfile.getProfile(context, "player");
        var playerContext = PlaceholderContext.of(context.getSource().getPlayer());


        var data = module.getData(PlayerUtils.getId(profile));

        var placeholders = Map.of(
                "home", Component.nullToEmpty(name),
                "owner", Component.nullToEmpty(PlayerUtils.getName(profile))
        );

        if (!data.homes.containsKey(name)) {
            context.getSource().sendSuccess(() ->
                    module.locale().get(
                            "homeNotFound",
                            playerContext,
                            placeholders
                    ), false);

            return 1;
        }

        context.getSource().sendSuccess(() ->
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
