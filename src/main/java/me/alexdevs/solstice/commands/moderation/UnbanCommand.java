package me.alexdevs.solstice.commands.moderation;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class UnbanCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("unban").redirect(dispatcher.getRoot().getChild("pardon")));
    }
}
