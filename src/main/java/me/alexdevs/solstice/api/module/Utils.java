package me.alexdevs.solstice.api.module;

import com.mojang.brigadier.CommandDispatcher;

public class Utils {
    public static void removeCommands(CommandDispatcher<?> dispatcher, String... commandNames) {
        for (String commandName : commandNames) {
            var command = dispatcher.getRoot().getChild(commandName);
            if (command != null) {
                dispatcher.getRoot().getChildren().remove(command);
            }
        }
    }
}
