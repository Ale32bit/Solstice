package me.alexdevs.solstice.modules.home.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.text.Components;
import me.alexdevs.solstice.modules.home.HomeModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class HomeCommand extends ModCommand<HomeModule> {
    public HomeCommand(HomeModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("home");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(true))
                .executes(context -> execute(context, "home", false))
                .then(argument("name", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            if (!context.getSource().isPlayer())
                                return SharedSuggestionProvider.suggest(new String[]{}, builder);

                            var data = module.getData(context.getSource().getPlayer().getUUID());

                            return SharedSuggestionProvider.suggest(data.homes.keySet().stream(), builder);
                        })
                        .executes(context -> execute(context, StringArgumentType.getString(context, "name"), false))
                        .then(literal("force")
                                .executes(context -> execute(context, StringArgumentType.getString(context, "name"), true))
                        ));
    }

    private int execute(CommandContext<CommandSourceStack> context, String name, boolean force) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        var data = module.getData(player.getUUID());
        var playerContext = PlaceholderContext.of(player);

        var placeholders = Map.of(
                "home", Component.nullToEmpty(name)
        );

        if (!data.homes.containsKey(name)) {
            context.getSource().sendSuccess(() ->
                    module.locale().get(
                            "homeNotFound",
                            playerContext,
                            placeholders
                    ), false);

            return 0;
        }

        context.getSource().sendSuccess(() ->
                module.locale().get(
                        "teleporting",
                        playerContext,
                        placeholders
                ), false);

        var homePosition = data.homes.get(name);

        if (force) {
            homePosition.teleport(player);
        } else {
            var success = homePosition.safeTeleport(player);
            if (!success) {
                var safePlaceholders = Map.of(
                        "forceHomeButton", Components.button(
                                module.locale().raw("forceLabel"),
                                module.locale().raw("forceHover"),
                                "/home " + name + " force"
                        ),
                        "home", Component.nullToEmpty(name)
                );

                context.getSource().sendSuccess(() -> module.locale().get(
                        "teleportFailed",
                        playerContext,
                        safePlaceholders
                ), false);
            }
        }


        return 1;
    }

}
