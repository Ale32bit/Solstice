package me.alexdevs.solstice.api.command.flags;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.List;

public abstract class ArgumentFlag<T> extends Flag {
    public ArgumentFlag(String name, List<Character> shortNames) {
        super(name, shortNames);
    }

    public abstract T getValue();

    @Override
    public abstract void accept(String input) throws CommandSyntaxException;

    @Override
    public boolean acceptsValue() {
        return true;
    }

    @Override
    public abstract ArgumentFlag<T> clone();
}
