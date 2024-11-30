package me.alexdevs.solstice.commands.warp;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.util.Format;
import com.mojang.brigadier.CommandDispatcher;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Map;

import static net.minecraft.server.command.CommandManager.literal;

public class WarpsCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("warps")
                .requires(Permissions.require("solstice.command.warps", true))
                .executes(context -> {
                    var source = context.getSource();
                    var serverState = Solstice.state.getServerState();
                    var warpList = serverState.warps.keySet().stream().toList();
                    var sourceContext = PlaceholderContext.of(source);

                    if(warpList.isEmpty()) {
                        context.getSource().sendFeedback(() -> Format.parse(
                                Solstice.locale().commands.warp.noWarps,
                                sourceContext
                        ), false);
                        return 1;
                    }

                    var listText = Text.empty();
                    var comma = Format.parse(Solstice.locale().commands.warp.warpsComma);
                    for(var i = 0; i < warpList.size(); i++) {
                        if (i > 0) {
                            listText = listText.append(comma);
                        }
                        var placeholders = Map.of(
                                "warp", Text.of(warpList.get(i))
                        );

                        listText = listText.append(Format.parse(
                                Solstice.locale().commands.warp.warpsFormat,
                                sourceContext,
                                placeholders
                        ));
                    }

                    var placeholders = Map.of(
                            "warpList", (Text) listText
                    );
                    context.getSource().sendFeedback(() -> Format.parse(
                            Solstice.locale().commands.warp.warpList,
                            sourceContext,
                            placeholders
                    ), false);

                    return 1;
                });

        dispatcher.register(rootCommand);
    }
}