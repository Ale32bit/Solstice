package me.alexdevs.solstice.mixin;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.events.CommandEvents;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public abstract class CommandManagerMixin {
    // Lnet/minecraft/server/command/CommandManager;execute(Lcom/mojang/brigadier/ParseResults;Ljava/lang/String;)V

    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    public void execute(ParseResults<ServerCommandSource> parseResults, String command, CallbackInfo ci) throws CommandSyntaxException {
        var context = parseResults.getContext();
        if (context.getSource() instanceof ServerCommandSource source) {
            if (!CommandEvents.ALLOW_COMMAND.invoker().allowCommand(source, command)) {
                ci.cancel();
            }

            CommandEvents.COMMAND.invoker().onCommand(source, command);
        }
    }
}
