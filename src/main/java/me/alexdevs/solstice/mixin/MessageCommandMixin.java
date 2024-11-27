package me.alexdevs.solstice.mixin;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.MessageCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(MessageCommand.class)
public class MessageCommandMixin {

    /**
     * @author AlexDevs
     * @reason Implementing custom tell command
     */
    @Overwrite
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

    }
}
