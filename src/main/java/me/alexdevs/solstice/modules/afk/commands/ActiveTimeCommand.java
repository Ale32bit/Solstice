package me.alexdevs.solstice.modules.afk.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.command.TimeSpan;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.afk.AfkModule;
import net.minecraft.command.argument.GameProfileArgumentType;
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
                .then(CommandManager.literal("leaderboard")
                        .requires(require("leaderboard", true))
                        .executes(context -> {
                            return 1;
                        })
                )
                .then(CommandManager.literal("player")
                        .then(CommandManager.argument("player", GameProfileArgumentType.gameProfile())
                                .requires(require("others", 1))
                                .executes(context -> {
                                    return 1;
                                })
                        ));
    }
}
