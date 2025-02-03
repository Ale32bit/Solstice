package me.alexdevs.solstice.api.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.command.flags.Flag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Flags {
    public static void parse(String input, Flag... flags) throws CommandSyntaxException {
        var args = parseArgs(input);
        parseFlags(args, Arrays.stream(flags).toList());
    }

    private static List<String> parseArgs(String input) {
        var args = new ArrayList<String>();
        var arg = new StringBuilder();
        var inQuotes = false;
        var escape = false;

        for (int i = 0; i < input.length(); i++) {
            var c = input.charAt(i);

            if (escape) {
                arg.append(c);
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ' ' && !inQuotes) {
                if (!arg.isEmpty()) {
                    args.add(arg.toString());
                    arg.setLength(0);
                }
            } else {
                arg.append(c);
            }
        }

        if (!arg.isEmpty()) {
            args.add(arg.toString());
        }

        return args;
    }

    private static void parseFlags(List<String> args, List<Flag> flags) throws CommandSyntaxException {
        var iter = args.iterator();
        while (iter.hasNext()) {
            var arg = iter.next();
            if (arg.startsWith("-")) {
                if (arg.startsWith("--")) {
                    var argName = arg.substring(2);
                    for (var flag : flags) {
                        if (argName.equals(flag.getName())) {
                            if (flag.acceptsValue()) {
                                flag.accept(iter.next());
                            } else {
                                flag.accept();
                            }
                        }
                    }
                } else {
                    var f = arg.substring(1);
                    for (var c : f.toCharArray()) {
                        for (var flag : flags) {
                            if (flag.getShortFlags().contains(c)) {
                                if (flag.acceptsValue()) {
                                    flag.accept(iter.next());
                                } else {
                                    flag.accept();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
