package me.alexdevs.solstice.modules.ignore.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.utils.PlayerUtils;
import me.alexdevs.solstice.modules.ignore.IgnoreModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class IgnoreCommand extends ModCommand<IgnoreModule> {
    public IgnoreCommand(IgnoreModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("ignore");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(true))
                .then(argument("target", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            var player = context.getSource().getPlayerOrException();
                            var playerManager = context.getSource().getServer().getPlayerList();
                            return SharedSuggestionProvider.suggest(Arrays.stream(playerManager.getPlayerNamesArray()).filter(s -> !s.equals(PlayerUtils.getName(player.getGameProfile()))), builder);
                        })
                        .executes(context -> {
                            var player = context.getSource().getPlayerOrException();
                            var targetName = StringArgumentType.getString(context, "target");
                            var profileOpt = PlayerUtils.getProfile(context.getSource().getServer(), targetName);
                            var playerContext = PlaceholderContext.of(player);

                            if (profileOpt.isEmpty()) {
                                context.getSource().sendSuccess(() -> module.locale().get("playerNotFound", playerContext), false);

                            } else {
                                var profile = profileOpt.get();

                                if (PlayerUtils.getId(profile).equals(PlayerUtils.getId(player.getGameProfile()))) {
                                    context.getSource().sendSuccess(() -> module.locale().get("targetIsSelf", playerContext), false);
                                } else {
                                    var playerData = module.getPlayerData(player.getUUID());
                                    var map = Map.of("targetName", Component.nullToEmpty(PlayerUtils.getName(profile)));

                                    if (playerData.ignoredPlayers.contains(PlayerUtils.getId(profile))) {
                                        playerData.ignoredPlayers.remove(PlayerUtils.getId(profile));
                                        context.getSource().sendSuccess(() -> module.locale().get("unblockedPlayer", playerContext, map), false);
                                    } else {
                                        playerData.ignoredPlayers.add(PlayerUtils.getId(profile));
                                        context.getSource().sendSuccess(() -> module.locale().get("blockedPlayer", playerContext, map), false);
                                    }
                                }
                            }
                            return 1;
                        }));
    }
}
