package me.alexdevs.solstice.api.command.flags;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.List;

public class StringFlag extends ArgumentFlag<String> {
    protected String value;
    protected final StringArgumentType type;
    public StringFlag(String name, List<Character> shortFlags) {
        super(name, shortFlags);
        type = StringArgumentType.string();
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void accept(String input) throws CommandSyntaxException {
        this.isUsed = true;
        value = type.parse(new StringReader(input));
    }

    public static String get(String name, List<Flag> flags) {
        for(var flag : flags) {
            if(flag.getName().equals(name) && flag instanceof StringFlag stringFlag) {
                return stringFlag.getValue();
            }
        }
        return null;
    }

    @Override
    public StringFlag clone() {
        return new StringFlag(name, shortFlags);
    }
}
