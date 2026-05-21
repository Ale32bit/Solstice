package me.alexdevs.solstice.modules.ban.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.command.TimeSpan;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.utils.ProfileOrNameAndId;
import me.alexdevs.solstice.modules.ban.BanModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.GameProfileArgument;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class TempBanCommand extends ModCommand<BanModule> {
    public TempBanCommand(BanModule module) {
        super(module);
    }

    private static Date getDateFromNow(int seconds) {
        var now = new Date();
        var c = Calendar.getInstance();
        c.setTime(now);
        c.add(Calendar.SECOND, seconds);
        return c.getTime();
    }

    @Override
    public List<String> getNames() {
        return List.of("tempban");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require("tempban", 3))
                .then(argument("targets", GameProfileArgument.gameProfile())
                        .then(argument("duration", StringArgumentType.string())
                                .suggests(TimeSpan::suggest)
                                .executes(context -> execute(context, GameProfileArgument.getGameProfiles(context, "targets").stream().map(ProfileOrNameAndId::new).toList(), null, TimeSpan.getTimeSpan(context, "duration")))
                                .then(argument("reason", StringArgumentType.greedyString())
                                        .executes(context -> execute(context, GameProfileArgument.getGameProfiles(context, "targets").stream().map(ProfileOrNameAndId::new).toList(), StringArgumentType.getString(context, "reason"), TimeSpan.getTimeSpan(context, "duration"))))));

    }

    private int execute(CommandContext<CommandSourceStack> context, Collection<ProfileOrNameAndId> targets, String reason, int duration) throws CommandSyntaxException {
        var expiryDate = getDateFromNow(duration);

        return BanCommand.execute(context, targets, reason, expiryDate);
    }
}
