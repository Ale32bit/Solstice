package me.alexdevs.solstice.modules.moderation.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.command.TimeSpan;
import me.alexdevs.solstice.api.module.ModCommand;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TempBanCommand extends ModCommand {
    public TempBanCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("tempban");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(3))
                .then(argument("targets", GameProfileArgumentType.gameProfile())
                        .then(argument("duration", StringArgumentType.string())
                                .suggests(TimeSpan::suggest)
                                .executes(context -> execute(context, GameProfileArgumentType.getProfileArgument(context, "targets"), null, TimeSpan.getTimeSpan(context, "duration")))
                                .then(argument("reason", StringArgumentType.greedyString())
                                        .executes(context -> execute(context, GameProfileArgumentType.getProfileArgument(context, "targets"), StringArgumentType.getString(context, "reason"), TimeSpan.getTimeSpan(context, "duration"))))));

    }

    private int execute(CommandContext<ServerCommandSource> context, Collection<GameProfile> targets, String reason, int duration) throws CommandSyntaxException {
        var expiryDate = getDateFromNow(duration);

        return BanCommand.execute(context, targets, reason, expiryDate);
    }

    private static Date getDateFromNow(int seconds) {
        var now = new Date();
        var c = Calendar.getInstance();
        c.setTime(now);
        c.add(Calendar.SECOND, seconds);
        return c.getTime();
    }
}
