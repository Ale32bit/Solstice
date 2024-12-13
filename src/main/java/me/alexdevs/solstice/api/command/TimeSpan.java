package me.alexdevs.solstice.api.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.alexdevs.solstice.Solstice;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// https://github.com/NucleusPowered/Nucleus/blob/v3/nucleus-core/src/main/java/io/github/nucleuspowered/nucleus/core/scaffold/command/parameter/TimespanParameter.java
public class TimeSpan {
    private static final Pattern minorTimeString = Pattern.compile("^\\d+$");
    private static final Pattern timeString = Pattern.compile("^((\\d+)w)?((\\d+)d)?((\\d+)h)?((\\d+)m)?((\\d+)s)?$");

    private static final int secondsInMinute = 60;
    private static final int secondsInHour = 60 * secondsInMinute;
    private static final int secondsInDay = 24 * secondsInHour;
    private static final int secondsInWeek = 7 * secondsInDay;

    private static long amount(@Nullable final String g, final int multiplier) {
        if (g != null && !g.isEmpty()) {
            return multiplier * Long.parseUnsignedLong(g);
        }

        return 0;
    }


    public static Optional<? extends Long> parse(String s) {
        // First, if just digits, return the number in seconds.
        if (minorTimeString.matcher(s).matches()) {
            return Optional.of(Long.parseUnsignedLong(s));
        }

        final Matcher m = timeString.matcher(s);
        if (m.matches()) {
            long time = amount(m.group(2), secondsInWeek);
            time += amount(m.group(4), secondsInDay);
            time += amount(m.group(6), secondsInHour);
            time += amount(m.group(8), secondsInMinute);
            time += amount(m.group(10), 1);

            if (time > 0) {
                return Optional.of(time);
            }
        }

        return Optional.empty();
    }

    public static CompletableFuture<Suggestions> suggest(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        Solstice.LOGGER.info("{}", builder.getRemainingLowerCase());

        for (int i = 0; i < 5; i++) {
            var original = builder.getRemainingLowerCase();
            if (minorTimeString.matcher(builder.getRemaining()).matches()) {
                builder.suggest(original);

                builder.suggest(original + "w");
                builder.suggest(original + "d");
                builder.suggest(original + "h");
                builder.suggest(original + "m");
                builder.suggest(original + "s");

                builder = builder.createOffset(builder.getInput().length());
            }
        }

        return builder.buildFuture();
    }
}
