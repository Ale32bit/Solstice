package me.alexdevs.solstice.modules.moderation.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.locale.Locale;
import me.alexdevs.solstice.modules.moderation.ModerationModule;
import me.alexdevs.solstice.util.Format;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.literal;

public class IgnoreListCommand extends ModCommand {
    private final Locale locale = Solstice.newLocaleManager.getLocale(ModerationModule.ID);

    public IgnoreListCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("ignorelist");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(true))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    var playerData = ModerationModule.getPlayerData(player.getUuid());
                    var ignoreList = playerData.ignoredPlayers;
                    var playerContext = PlaceholderContext.of(player);

                    if (ignoreList.isEmpty()) {
                        context.getSource().sendFeedback(() -> locale.get("ignoreListEmpty",
                                playerContext
                        ), false);
                        return 1;
                    }

                    var listText = Text.empty();
                    var comma = locale.get("ignoreListComma");
                    for (var i = 0; i < ignoreList.size(); i++) {
                        if (i > 0) {
                            listText = listText.append(comma);
                        }

                        String playerName;
                        var gameProfile = context.getSource().getServer().getUserCache().getByUuid(ignoreList.get(i));
                        if (gameProfile.isPresent()) {
                            playerName = gameProfile.get().getName();
                        } else {
                            playerName = ignoreList.get(i).toString();
                        }

                        var placeholders = Map.of(
                                "player", Text.of(playerName)
                        );

                        listText = listText.append(locale.get(
                                "ignoreListFormat",
                                playerContext,
                                placeholders
                        ));
                    }

                    var placeholders = Map.of(
                            "playerList", (Text) listText
                    );
                    context.getSource().sendFeedback(() -> locale.get(
                            "ignoreList",
                            playerContext,
                            placeholders
                    ), false);

                    return 1;
                });
    }
}
