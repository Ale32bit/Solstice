package me.alexdevs.solstice.modules.afk.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.command.LocalGameProfile;
import me.alexdevs.solstice.api.command.TimeSpan;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.afk.AfkModule;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

public class ActiveTimeCommand extends ModCommand<AfkModule> {
    public ActiveTimeCommand(AfkModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("activetime");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return CommandManager.literal(name)
                .requires(require(true))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    var activeTime = module.getActiveTime(player.getUuid());

                    var longSpan = TimeSpan.toLongString(activeTime);

                    var map = Map.of(
                            "activeTime", Text.of(longSpan),
                            "player", player.getName()
                    );

                    context.getSource().sendFeedback(() -> module.locale().get("yourActiveTime", map), false);

                    return 1;
                })
                .then(CommandManager.literal("player")
                        .requires(require("others", 1))
                        .then(CommandManager.argument("player", StringArgumentType.word())
                                .suggests(LocalGameProfile::suggest)
                                .executes(context -> {
                                    var profile = LocalGameProfile.getProfile(context, "player");
                                    var activeTime = module.getActiveTime(profile.getId());

                                    if (activeTime == 0) {
                                        context.getSource().sendFeedback(() -> module.locale().get("neverPlayed"), false);
                                        return 0;
                                    }

                                    var longSpan = TimeSpan.toLongString(activeTime);

                                    var map = Map.of(
                                            "activeTime", Text.of(longSpan),
                                            "player", Text.of(profile.getName())
                                    );

                                    context.getSource().sendFeedback(() -> module.locale().get("playerActiveTime", map), false);

                                    return 1;
                                })
                        ))
                .then(CommandManager.literal("leaderboard")
                        .requires(require("leaderboard", true))
                        .executes(context -> {
                            var leaderboard = module.getActiveTimeLeaderboard();

                            var text = Text.empty();

                            text.append(module.locale().get("leaderboardHeader"));

                            var index = 0;
                            for (var entry : leaderboard) {
                                text.append("\n");
                                index++;
                                var map = Map.of(
                                        "index", Text.of(String.valueOf(index)),
                                        "player", Text.of(entry.name()),
                                        "uuid", Text.of(entry.uuid().toString()),
                                        "time", Text.of(TimeSpan.toLongString(entry.activeTime())),
                                        "shortTime", Text.of(TimeSpan.toShortString(entry.activeTime())),
                                        "seconds", Text.of(String.valueOf(entry.activeTime()))
                                );
                                text.append(module.locale().get("leaderboardEntry", map));
                            }

                            context.getSource().sendFeedback(() -> text, false);

                            return 1;
                        })
                );
    }
}
