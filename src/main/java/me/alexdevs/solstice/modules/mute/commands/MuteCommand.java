package me.alexdevs.solstice.modules.mute.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.command.TimeSpan;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.mute.MuteModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class MuteCommand extends ModCommand<MuteModule> {
    public MuteCommand(MuteModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("mute");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(2))
                .then(argument("targets", GameProfileArgument.gameProfile())
                        .executes(context -> execute(context, 0))
                        .then(argument("timespan", TimeSpan.timeSpan())
                                .suggests(TimeSpan::suggest)
                                .executes(context -> execute(context, TimeSpan.getTimeSpan(context, "timespan"))))
                );
    }

    private int execute(CommandContext<CommandSourceStack> context, int timespan) throws CommandSyntaxException {
        var targets = GameProfileArgument.getGameProfiles(context, "targets");

        var calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, timespan);
        var date = calendar.getTime();

        targets.forEach(profile -> {
            var playerData = module.getPlayerData(profile.getId());
            playerData.muted = true;
            if (timespan != 0) {
                playerData.mutedUntil = date;
            }
        });

        Solstice.playerData.saveAll();

        String localeKey;
        if (timespan == 0) {
            if (targets.size() == 1) {
                localeKey = "muted";
            } else {
                localeKey = "mutedMultiple";
            }
        } else {
            if (targets.size() == 1) {
                localeKey = "mutedTimespan";
            } else {
                localeKey = "mutedMultipleTimespan";

            }
        }

        var placeholders = Map.of(
                "count", Component.nullToEmpty(String.valueOf(targets.size())),
                "player", Component.nullToEmpty(targets.stream().findFirst().get().getName()),
                "timespan", Component.nullToEmpty(TimeSpan.toLongString(timespan))
        );

        context.getSource().sendSuccess(() -> module.locale().get(localeKey, placeholders), true);

        return 1;
    }
}
