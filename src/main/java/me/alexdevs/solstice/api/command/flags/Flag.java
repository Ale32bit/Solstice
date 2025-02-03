package me.alexdevs.solstice.api.command.flags;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Flag implements Comparable<Flag> {
    public static final SimpleCommandExceptionType UNEXPECTED_VALUE = new SimpleCommandExceptionType(new LiteralMessage("Unexpected value"));

    protected final String name;
    protected final List<Character> shortFlags = new ArrayList<>();
    protected boolean isUsed = false;

    public Flag(String name, List<Character> shortNames) {
        this.name = name;
        this.shortFlags.addAll(shortNames);
    }

    public String getName() {
        return name;
    }

    public List<Character> getShortFlags() {
        return Collections.unmodifiableList(shortFlags);
    }

    public boolean acceptsValue() {
        return false;
    }

    public boolean isUsed() {
        return isUsed;
    }

    @Override
    public Flag clone() {
        return new Flag(name, shortFlags);
    }


    public void accept(String value) throws CommandSyntaxException {
        throw UNEXPECTED_VALUE.create();
    }

    public void accept() {
        isUsed = true;
    }

    public static Flag of(String name) {
        return new Flag(name, Collections.emptyList());
    }

    @Override
    public int compareTo(@NotNull Flag o) {
        return this.name.compareTo(o.name);
    }
}
