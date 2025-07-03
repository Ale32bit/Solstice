package me.alexdevs.solstice.modules.afk.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.command.LocalGameProfile;
import me.alexdevs.solstice.api.command.TimeSpan;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.afk.AfkModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

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
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return Commands.literal(name)
                .requires(require(true))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrException();
                    var activeTime = module.getActiveTime(player.getUUID());

                    var longSpan = TimeSpan.toLongString(activeTime);

                    var map = Map.of(
                            "activeTime", Component.nullToEmpty(longSpan),
                            "player", player.getName()
                    );

                    context.getSource().sendSuccess(() -> module.locale().get("yourActiveTime", map), false);

                    return 1;
                })
                .then(Commands.literal("player")
                        .requires(require("others", 1))
                        .then(Commands.argument("player", StringArgumentType.word())
                                .suggests(LocalGameProfile::suggest)
                                .executes(context -> {
                                    var profile = LocalGameProfile.getProfile(context, "player");
                                    var activeTime = module.getActiveTime(profile.getId());

                                    if (activeTime == 0) {
                                        context.getSource().sendSuccess(() -> module.locale().get("neverPlayed"), false);
                                        return 0;
                                    }

                                    var longSpan = TimeSpan.toLongString(activeTime);

                                    var map = Map.of(
                                            "activeTime", Component.nullToEmpty(longSpan),
                                            "player", Component.nullToEmpty(profile.getName())
                                    );

                                    context.getSource().sendSuccess(() -> module.locale().get("playerActiveTime", map), false);

                                    return 1;
                                })
                        ))
                .then(Commands.literal("leaderboard")
                        .requires(require("leaderboard", true))
                        .executes(context -> {
                            var leaderboard = module.getActiveTimeLeaderboard();

                            var text = Component.empty();

                            text.append(module.locale().get("leaderboardHeader"));

                            var index = 0;
                            for (var entry : leaderboard) {
                                text.append("\n");
                                index++;
                                var map = Map.of(
                                        "index", Component.nullToEmpty(String.valueOf(index)),
                                        "player", Component.nullToEmpty(entry.name()),
                                        "uuid", Component.nullToEmpty(entry.uuid().toString()),
                                        "time", Component.nullToEmpty(TimeSpan.toLongString(entry.activeTime())),
                                        "shortTime", Component.nullToEmpty(TimeSpan.toShortString(entry.activeTime())),
                                        "seconds", Component.nullToEmpty(String.valueOf(entry.activeTime()))
                                );
                                text.append(module.locale().get("leaderboardEntry", map));
                            }

                            context.getSource().sendSuccess(() -> text, false);

                            return 1;
                        })
                )
                .then(Commands.literal("set")
                        .requires(require("set", 3))
                        .then(Commands.argument("player", StringArgumentType.word())
                                .suggests(LocalGameProfile::suggest)
                                .then(Commands.argument("time", TimeSpan.timeSpan())
                                        .suggests(TimeSpan::suggest)
                                        .executes(context -> {
                                            var profile = LocalGameProfile.getProfile(context, "player");
                                            var time = TimeSpan.getTimeSpan(context, "time");

                                            var data = module.getPlayerData(profile.getId());
                                            data.activeTime = time;

                                            var map = Map.of(
                                                    "player", Component.nullToEmpty(profile.getName()),
                                                    "time", Component.nullToEmpty(TimeSpan.toLongString(time))
                                            );
                                            context.getSource().sendSuccess(() -> module.locale().get("activeTimeSet", map), true);

                                            return 1;
                                        })
                                ))
                )
                .then(Commands.literal("recalculate")
                        .requires(require("recalculate", 2))
                        .executes(context -> {
                            module.forceRecalculateLeaderboard();

                            context.getSource().sendSuccess(() -> module.locale().get("leaderboardRecalculated"), true);

                            return 1;
                        }));
    }
}
