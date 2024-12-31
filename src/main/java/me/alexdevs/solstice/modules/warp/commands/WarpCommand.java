package me.alexdevs.solstice.modules.warp.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.warp.WarpModule;
import me.alexdevs.solstice.modules.warp.data.WarpServerData;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class WarpCommand extends ModCommand<WarpModule> {
    public WarpCommand(WarpModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("warp");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(true))
                .then(argument("name", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            if (!context.getSource().isExecutedByPlayer())
                                return CommandSource.suggestMatching(new String[]{}, builder);

                            var serverData = Solstice.serverData.getData(WarpServerData.class);
                            var player = context.getSource().getPlayer();
                            var warps = serverData.warps.keySet().stream().filter(serverPosition -> module.canUseWarp(player, serverPosition));
                            return CommandSource.suggestMatching(warps, builder);
                        })
                        .executes(context -> execute(context, StringArgumentType.getString(context, "name"))));
    }

    private int execute(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        var serverDate = Solstice.serverData.getData(WarpServerData.class);
        var warps = serverDate.warps;
        var playerContext = PlaceholderContext.of(player);

        var placeholders = Map.of(
                "warp", Text.of(name)
        );

        if (!warps.containsKey(name)) {
            context.getSource().sendFeedback(() -> module.locale().get(
                    "warpNotFound",
                    playerContext,
                    placeholders

            ), false);
            return 0;
        }

        if (!module.canUseWarp(player, name)) {
            context.getSource().sendFeedback(() -> module.locale().get(
                    "noPermission",
                    playerContext,
                    placeholders
            ), false);

            return 0;
        }

        context.getSource().sendFeedback(() -> module.locale().get(
                "teleporting",
                playerContext,
                placeholders
        ), false);

        var warpPosition = warps.get(name);
        warpPosition.teleport(player);

        return 1;
    }
}
