package me.alexdevs.solstice.api.command;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// https://github.com/NucleusPowered/Nucleus/blob/v3/nucleus-core/src/main/java/io/github/nucleuspowered/nucleus/core/scaffold/command/parameter/TimespanParameter.java
public class TimeSpan {
    private static final Pattern minorTimeString = Pattern.compile("^\\d+$");
    private static final Pattern timeString = Pattern.compile("^((\\d+)w)?((\\d+)d)?((\\d+)h)?((\\d+)m)?((\\d+)s)?$");
    private static final Pattern timeStringNoEnd = Pattern.compile("^((\\d+)w)?((\\d+)d)?((\\d+)h)?((\\d+)m)?((\\d+)s)?");
    private static final Pattern lastDigits = Pattern.compile("\\d+$");

    private static final int secondsInMinute = 60;
    private static final int secondsInHour = 60 * secondsInMinute;
    private static final int secondsInDay = 24 * secondsInHour;
    private static final int secondsInWeek = 7 * secondsInDay;

    public static final SimpleCommandExceptionType INVALID_TIMESPAN = new SimpleCommandExceptionType(new LiteralMessage("Invalid timespan"));

    private static int amount(@Nullable final String g, final int multiplier) {
        if (g != null && !g.isEmpty()) {
            return multiplier * Integer.parseUnsignedInt(g);
        }

        return 0;
    }


    public static Optional<? extends Integer> parse(String s) {
        // First, if just digits, return the number in seconds.
        if (minorTimeString.matcher(s).matches()) {
            return Optional.of(Integer.parseUnsignedInt(s));
        }

        final Matcher m = timeString.matcher(s);
        if (m.matches()) {
            int time = amount(m.group(2), secondsInWeek);
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
        var original = builder.getRemainingLowerCase();
        if (original.isEmpty()) {
            return Suggestions.empty();
        }

        if (timeString.matcher(original).matches()) {
            builder.suggest(original);
            return builder.buildFuture();
        }

        var units = List.of(
                new Unit("w", "Week"),
                new Unit("d", "Day"),
                new Unit("h", "Hour"),
                new Unit("m", "Minute"),
                new Unit("s", "Second")
        );

        if (minorTimeString.matcher(original).matches()) {
            for (var unit : units) {
                builder.suggest(original + unit.unit, new LiteralMessage(unit.tooltip));
            }
            return builder.buildFuture();
        }


        if (timeStringNoEnd.matcher(original).find() && lastDigits.matcher(original).find()) {
            var max = 0;
            for (var i = 0; i < units.size(); i++) {
                if (original.contains(units.get(i).unit))
                    max = i + 1;
            }

            for (var i = max; i < units.size(); i++) {
                var unit = units.get(i);
                if (!original.contains(unit.unit)) {
                    builder.suggest(original + unit.unit, new LiteralMessage(unit.tooltip));
                }
            }
        }



        return builder.buildFuture();
    }

    private record Unit(String unit, String tooltip) {

    }
}
