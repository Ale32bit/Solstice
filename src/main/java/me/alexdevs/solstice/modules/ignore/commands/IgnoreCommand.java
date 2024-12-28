package me.alexdevs.solstice.modules.ignore.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.ignore.IgnoreModule;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class IgnoreCommand extends ModCommand<IgnoreModule> {
    public IgnoreCommand(IgnoreModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("ignore");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(true))
                .then(argument("target", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            var player = context.getSource().getPlayerOrThrow();
                            var playerManager = context.getSource().getServer().getPlayerManager();
                            return CommandSource.suggestMatching(Arrays.stream(playerManager.getPlayerNames()).filter(s -> !s.equals(player.getGameProfile().getName())), builder);
                        })
                        .executes(context -> {
                            var player = context.getSource().getPlayerOrThrow();

                            var targetName = StringArgumentType.getString(context, "target");

                            context.getSource().getServer().getUserCache().findByNameAsync(targetName).thenAcceptAsync(profileOpt -> {
                                var playerContext = PlaceholderContext.of(player);

                                if (profileOpt.isEmpty()) {
                                    context.getSource().sendFeedback(() -> module.locale().get("playerNotFound", playerContext), false);
                                    return;
                                }

                                var profile = profileOpt.get();

                                if (profile.getId().equals(player.getGameProfile().getId())) {
                                    context.getSource().sendFeedback(() -> module.locale().get("targetIsSelf", playerContext), false);
                                    return;
                                }

                                var playerData = module.getPlayerData(player.getUuid());

                                var map = Map.of("targetName", Text.of(profile.getName()));

                                if (playerData.ignoredPlayers.contains(profile.getId())) {
                                    playerData.ignoredPlayers.remove(profile.getId());
                                    context.getSource().sendFeedback(() -> module.locale().get("unblockedPlayer", playerContext, map), false);

                                } else {
                                    playerData.ignoredPlayers.add(profile.getId());
                                    context.getSource().sendFeedback(() -> module.locale().get("blockedPlayer", playerContext, map), false);
                                }
                            });

                            return 1;
                        }));
    }
}
