package me.alexdevs.solstice.api.command.flags;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.List;

public class FloatFlag extends ArgumentFlag<Float> {
    protected Float value;
    protected final FloatArgumentType type;

    public FloatFlag(String name, List<Character> shortFlags) {
        super(name, shortFlags);
        type = FloatArgumentType.floatArg();
    }

    @Override
    public Float getValue() {
        return value;
    }

    @Override
    public void accept(String input) throws CommandSyntaxException {
        this.isUsed = true;
        value = type.parse(new StringReader(input));
    }

    @Override
    public FloatFlag clone() {
        return new FloatFlag(name, shortFlags);
    }
}
