package me.alexdevs.solstice.mixin.events;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.events.CommandEvents;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandDispatcher.class)
public class CommandEventsMixin<S> {
    @Inject(method = "parse(Lcom/mojang/brigadier/StringReader;Ljava/lang/Object;)Lcom/mojang/brigadier/ParseResults;", at = @At("HEAD"), remap = false, cancellable = true)
    public void execute(StringReader reader, S source, CallbackInfoReturnable<ParseResults<S>> cir) throws CommandSyntaxException {
        if (source instanceof CommandSourceStack stack) {
            var command = reader.getString();
            if (!CommandEvents.ALLOW_COMMAND.invoker().allowCommand(stack, command)) {
                cir.cancel();
            }

            CommandEvents.COMMAND.invoker().onCommand(stack, command);
        }
    }
}
