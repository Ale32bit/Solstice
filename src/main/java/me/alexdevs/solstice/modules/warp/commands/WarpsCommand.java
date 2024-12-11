package me.alexdevs.solstice.modules.warp.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.warp.WarpModule;
import me.alexdevs.solstice.modules.warp.data.WarpServerData;
import com.mojang.brigadier.CommandDispatcher;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.literal;

public class WarpsCommand extends ModCommand {
    public WarpsCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("warps");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(true))
                .executes(context -> {
                    var source = context.getSource();
                    var serverDate = Solstice.serverData.getData(WarpServerData.class);
                    var warpList = serverDate.warps.keySet().stream().toList();
                    var sourceContext = PlaceholderContext.of(source);

                    var locale = Solstice.localeManager.getLocale(WarpModule.ID);

                    if(warpList.isEmpty()) {
                        context.getSource().sendFeedback(() -> locale.get(
                                "noWarps",
                                sourceContext
                        ), false);
                        return 1;
                    }

                    var listText = Text.empty();
                    var comma = locale.get("warpsComma");
                    for(var i = 0; i < warpList.size(); i++) {
                        if (i > 0) {
                            listText = listText.append(comma);
                        }
                        var placeholders = Map.of(
                                "warp", Text.of(warpList.get(i))
                        );

                        listText = listText.append(locale.get(
                                "warpsFormat",
                                sourceContext,
                                placeholders
                        ));
                    }

                    var placeholders = Map.of(
                            "warpList", (Text) listText
                    );
                    context.getSource().sendFeedback(() -> locale.get(
                            "warpList",
                            sourceContext,
                            placeholders
                    ), false);

                    return 1;
                });
    }
}