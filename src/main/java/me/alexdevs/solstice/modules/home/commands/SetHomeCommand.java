package me.alexdevs.solstice.modules.home.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.home.HomeModule;
import me.alexdevs.solstice.api.text.Components;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SetHomeCommand extends ModCommand<HomeModule> {
    public SetHomeCommand(HomeModule module) {
        super(module);
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
        var data = module.getData(player.getUuid());
        var homes = data.homes;
        var playerContext = PlaceholderContext.of(player);

        var placeholders = Map.of(
                "home", Text.of(name),
                "forceSetButton", Components.button(
                        module.locale().raw("forceSetLabel"),
                        module.locale().raw("forceSetHover"),
                        "/sethome " + name + " force"
                )
        );

        var exists = homes.containsKey(name);
        if (exists && !forced) {
            var text = module.locale().get(
                    "homeExists",
                    playerContext,
                    placeholders
            );

            context.getSource().sendFeedback(() -> text, false);

            return 0;
        }

        homes.remove(name);

        var groups = module.getConfig().homes;
        var maxHomes = Integer.MIN_VALUE;
        for(var entry : groups.entrySet()) {
            var group = entry.getKey();
            if(Permissions.check(player, "group." + group)) {
                maxHomes = Math.max(maxHomes, entry.getValue());
            }
        }

        var allowUnlimited = Permissions.check(player, getPermissionNode("unlimited"), 3);
        if (!allowUnlimited && homes.size() >= maxHomes) {
            context.getSource().sendFeedback(() -> module.locale().get(
                    "maxHomesReached",
                    playerContext,
                    placeholders
            ), false);
            return 0;
        }

        var homePosition = new ServerLocation(player);
        homes.put(name, homePosition);

        context.getSource().sendFeedback(() -> module.locale().get(
                "homeSetSuccess",
                playerContext,
                placeholders
        ), false);

        return 1;
    }
}
