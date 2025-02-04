package me.alexdevs.solstice.api.command.flags;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.List;

public class DoubleFlag extends ArgumentFlag<Double> {
    protected double value;
    protected final DoubleArgumentType type;
    public DoubleFlag(String name, List<Character> shortFlags) {
        super(name, shortFlags);
        type = DoubleArgumentType.doubleArg();
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public void accept(String input) throws CommandSyntaxException {
        this.isUsed = true;
        value = type.parse(new StringReader(input));
    }

    @Override
    public DoubleFlag clone() {
        return new DoubleFlag(name, shortFlags);
    }
}
