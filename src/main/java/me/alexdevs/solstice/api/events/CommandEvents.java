package me.alexdevs.solstice.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.commands.CommandSourceStack;

public class CommandEvents {
    public static final Event<AllowCommand> ALLOW_COMMAND = EventFactory.createArrayBacked(AllowCommand.class, callbacks ->
            (source, command) -> {
                for (var callback : callbacks) {
                    if(!callback.allowCommand(source, command))
                        return false;
                }
                return true;
            });

    public static final Event<Command> COMMAND = EventFactory.createArrayBacked(Command.class, callbacks ->
            (player, command) -> {
                for (var callback : callbacks) {
                    callback.onCommand(player, command);
                }
            });

    @FunctionalInterface
    public interface AllowCommand {
        boolean allowCommand(CommandSourceStack source, String command);
    }

    @FunctionalInterface
    public interface Command {
        void onCommand(CommandSourceStack source, String command);
    }
}
