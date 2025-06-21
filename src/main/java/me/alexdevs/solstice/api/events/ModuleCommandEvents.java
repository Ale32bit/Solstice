package me.alexdevs.solstice.api.events;

import com.mojang.brigadier.context.CommandContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.module.ModuleBase;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.commands.CommandSourceStack;

public class ModuleCommandEvents {
    /**
     * This event is used on Solstice commands.
     */
    public static final Event<AllowCommand> ALLOW_COMMAND = EventFactory.createArrayBacked(AllowCommand.class, callbacks ->
            (node, context, command) -> {
                for (var callback : callbacks) {
                    if (!callback.allowCommand(node, context, command))
                        return false;
                }
                return true;
            });

    /**
     * This event is used on Solstice commands.
     */
    public static final Event<Command> COMMAND = EventFactory.createArrayBacked(Command.class, callbacks ->
            (node, context, command) -> {
                for (var callback : callbacks) {
                    callback.onCommand(node, context, command);
                }
            });

    @FunctionalInterface
    public interface AllowCommand {
        boolean allowCommand(String node, CommandContext<CommandSourceStack> context, ModCommand<? extends ModuleBase> command);
    }

    @FunctionalInterface
    public interface Command {
        void onCommand(String node, CommandContext<CommandSourceStack> context, ModCommand<? extends ModuleBase> command);
    }
}
