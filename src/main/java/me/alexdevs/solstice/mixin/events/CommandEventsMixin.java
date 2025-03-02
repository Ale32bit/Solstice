package me.alexdevs.solstice.mixin.events;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.events.CommandEvents;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandDispatcher.class)
public class CommandEventsMixin<S> {
    @Inject(method = "execute(Lcom/mojang/brigadier/ParseResults;)I", at = @At("HEAD"), remap = false, cancellable = true)
    public void execute(final ParseResults<S> parse, CallbackInfoReturnable<Integer> cir) throws CommandSyntaxException {
        var context = parse.getContext();
        if (context.getSource() instanceof CommandSourceStack source) {
            var command = parse.getReader().getString();
            if (!CommandEvents.ALLOW_COMMAND.invoker().allowCommand(source, command)) {
                cir.setReturnValue(0);
            }

            CommandEvents.COMMAND.invoker().onCommand(source, command);
        }
    }
}
