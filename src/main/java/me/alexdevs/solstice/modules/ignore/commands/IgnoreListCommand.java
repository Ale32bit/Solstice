package me.alexdevs.solstice.modules.ignore.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.utils.PlayerUtils;
import me.alexdevs.solstice.modules.ignore.IgnoreModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.literal;

public class IgnoreListCommand extends ModCommand<IgnoreModule> {
    public IgnoreListCommand(IgnoreModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("ignorelist");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(true))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrException();
                    var playerData = module.getPlayerData(player.getUUID());
                    var ignoreList = playerData.ignoredPlayers;
                    var playerContext = PlaceholderContext.of(player);

                    if (ignoreList.isEmpty()) {
                        context.getSource().sendSuccess(() -> module.locale().get("ignoreListEmpty",
                                playerContext
                        ), false);
                        return 1;
                    }

                    var listText = Component.empty();
                    var comma = module.locale().get("ignoreListComma");
                    for (var i = 0; i < ignoreList.size(); i++) {
                        if (i > 0) {
                            listText = listText.append(comma);
                        }

                        String playerName;
                        var gameProfile = PlayerUtils.getProfile(context.getSource().getServer(),ignoreList.get(i));
                        if (gameProfile.isPresent()) {
                            playerName = PlayerUtils.getName(gameProfile.get());
                        } else {
                            playerName = ignoreList.get(i).toString();
                        }

                        var placeholders = Map.of(
                                "player", Component.nullToEmpty(playerName)
                        );

                        listText = listText.append(module.locale().get(
                                "ignoreListFormat",
                                playerContext,
                                placeholders
                        ));
                    }

                    var placeholders = Map.of(
                            "playerList", (Component) listText
                    );
                    context.getSource().sendSuccess(() -> module.locale().get(
                            "ignoreList",
                            playerContext,
                            placeholders
                    ), false);

                    return 1;
                });
    }
}
