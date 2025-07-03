package me.alexdevs.solstice.api.command;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.core.coreModule.CoreModule;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// https://github.com/NucleusPowered/Nucleus/blob/v3/nucleus-core/src/main/java/io/github/nucleuspowered/nucleus/core/scaffold/command/parameter/TimespanParameter.java
public class TimeSpan {
    public static final SimpleCommandExceptionType INVALID_TIMESPAN = new SimpleCommandExceptionType(new LiteralMessage("Invalid timespan"));
    private static final Pattern minorTimeString = Pattern.compile("^\\d+$");
    private static final Pattern timeString = Pattern.compile("^((\\d+)w)?((\\d+)d)?((\\d+)h)?((\\d+)m)?((\\d+)s)?$");
    private static final Pattern timeStringNoEnd = Pattern.compile("^((\\d+)w)?((\\d+)d)?((\\d+)h)?((\\d+)m)?((\\d+)s)?");
    private static final Pattern lastDigits = Pattern.compile("\\d+$");
    private static final int secondsInMinute = 60;
    private static final int secondsInHour = 60 * secondsInMinute;
    private static final int secondsInDay = 24 * secondsInHour;
    private static final int secondsInWeek = 7 * secondsInDay;
    private static final int secondsInMonth = 30 * secondsInDay;
    private static final int secondsInYear = 365 * secondsInDay;

    private static int amount(@Nullable final String g, final int multiplier) {
        if (g != null && !g.isEmpty()) {
            return multiplier * Integer.parseUnsignedInt(g);
        }

        return 0;
    }

    public static String toShortString(int total) {
        var builder = new StringBuilder();

        if(total >= secondsInWeek) {
            builder.append(total / secondsInWeek);
            builder.append("w");
            total %= secondsInWeek;
        }

        if(total >= secondsInDay) {
            builder.append(total / secondsInDay);
            builder.append("d");
            total %= secondsInDay;
        }

        if(total >= secondsInHour) {
            builder.append(total / secondsInHour);
            builder.append("h");
            total %= secondsInHour;
        }

        if(total >= secondsInMinute) {
            builder.append(total / secondsInMinute);
            builder.append("m");
            total %= secondsInMinute;
        }

        if(total > 0) {
            builder.append(total);
            builder.append("s");
        }

        return builder.toString();
    }

    private static String fill(String locale, int unit) {
        return locale.replaceAll("\\$\\{n}", String.valueOf(unit));
    }

    public static String toLongString(int total) {
        var builder = new StringBuilder();
        var locale = Solstice.localeManager.getShared();

        var prependSpace = false;

        if(total >= secondsInYear) {
            var value = total / secondsInYear;
            if(value == 1) {
                builder.append(fill(locale.raw("~unit.year"), value));
            } else {
                builder.append(fill(locale.raw("~unit.years"), value));
            }
            total %= secondsInYear;
            prependSpace = true;
        }

        if(total >= secondsInMonth) {
            if(prependSpace) {
                builder.append(" ");
            }
            var value = total / secondsInMonth;
            if(value == 1) {
                builder.append(fill(locale.raw("~unit.month"), value));
            } else {
                builder.append(fill(locale.raw("~unit.months"), value));
            }
            total %= secondsInMonth;
            prependSpace = true;
        }

        if(total >= secondsInWeek) {
            if(prependSpace) {
                builder.append(" ");
            }
            var value = total / secondsInWeek;
            if(value == 1) {
                builder.append(fill(locale.raw("~unit.week"), value));
            } else {
                builder.append(fill(locale.raw("~unit.weeks"), value));
            }
            total %= secondsInWeek;
            prependSpace = true;
        }

        if(total >= secondsInDay) {
            if(prependSpace) {
                builder.append(" ");
            }
            var value = total / secondsInDay;
            if(value == 1) {
                builder.append(fill(locale.raw("~unit.day"), value));
            } else {
                builder.append(fill(locale.raw("~unit.days"), value));
            }
            total %= secondsInDay;
            prependSpace = true;
        }

        if(total >= secondsInHour) {
            if(prependSpace) {
                builder.append(" ");
            }
            var value = total / secondsInHour;
            if(value == 1) {
                builder.append(fill(locale.raw("~unit.hour"), value));
            } else {
                builder.append(fill(locale.raw("~unit.hours"), value));
            }
            total %= secondsInHour;
            prependSpace = true;
        }

        if(total >= secondsInMinute) {
            if(prependSpace) {
                builder.append(" ");
            }
            var value = total / secondsInMinute;
            if(value == 1) {
                builder.append(fill(locale.raw("~unit.minute"), value));
            } else {
                builder.append(fill(locale.raw("~unit.minutes"), value));
            }
            total %= secondsInMinute;
            prependSpace = true;
        }

        if(total > 0) {
            if(prependSpace) {
                builder.append(" ");
            }
            if(total == 1) {
                builder.append(fill(locale.raw("~unit.second"), total));
            } else {
                builder.append(fill(locale.raw("~unit.seconds"), total));
            }
        }

        return builder.toString();
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

    public static int getTimeSpan(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        var argument = context.getArgument(name, String.class);
        var timespan = parse(argument);
        if (timespan.isPresent()) {
            return timespan.get();
        }

        throw INVALID_TIMESPAN.create();
    }

    public static CompletableFuture<Suggestions> suggest(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
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

    public static StringArgumentType timeSpan() {
        return StringArgumentType.word();
    }

    private record Unit(String unit, String tooltip) {

    }
}
