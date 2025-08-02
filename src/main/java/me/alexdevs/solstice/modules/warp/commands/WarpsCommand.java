package me.alexdevs.solstice.modules.warp.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.warp.WarpModule;
import me.alexdevs.solstice.modules.warp.data.WarpServerData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.literal;

public class WarpsCommand extends ModCommand<WarpModule> {
    public WarpsCommand(WarpModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("warps");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(true))
                .executes(context -> {
                    var source = context.getSource();
                    var serverDate = Solstice.serverData.getData(WarpServerData.class);
                    var warpList = serverDate.warps.keySet().stream().sorted().toList();
                    var sourceContext = PlaceholderContext.of(source);

                    if(source.isPlayer()) {
                        var player = source.getPlayer();
                        warpList = warpList.stream().filter(warp -> module.canUseWarp(player, warp)).toList();
                    }

                    if (warpList.isEmpty()) {
                        context.getSource().sendSuccess(() -> module.locale().get(
                                "noWarps",
                                sourceContext
                        ), false);
                        return 1;
                    }

                    var listText = Component.empty();
                    var comma = module.locale().get("warpsComma");
                    for (var i = 0; i < warpList.size(); i++) {
                        if (i > 0) {
                            listText = listText.append(comma);
                        }
                        var placeholders = Map.of(
                                "warp", Component.nullToEmpty(warpList.get(i))
                        );

                        listText = listText.append(module.locale().get(
                                "warpsFormat",
                                sourceContext,
                                placeholders
                        ));
                    }

                    var placeholders = Map.of(
                            "warpList", (Component) listText
                    );
                    context.getSource().sendSuccess(() -> module.locale().get(
                            "warpList",
                            sourceContext,
                            placeholders
                    ), false);

                    return 1;
                });
    }
}