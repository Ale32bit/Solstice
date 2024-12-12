package me.alexdevs.solstice.api.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Arrays;
import java.util.Collection;

public class TimeSpanArgumentType implements ArgumentType<TimeSpanArgumentType.TimeSpanArgument> {
    private static final Collection<String> EXAMPLES = Arrays.asList("60", "60s", "1w2d3h4m5s", "2d12h");

    public static TimeSpanArgumentType timeSpan() {
        return new TimeSpanArgumentType();
    }

    @Override
    public TimeSpanArgument parse(StringReader reader) throws CommandSyntaxException {
        return null;
    }

    public static class TimeSpanArgument {

    }
}
