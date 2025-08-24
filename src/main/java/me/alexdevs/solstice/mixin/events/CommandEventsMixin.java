package me.alexdevs.solstice.mixin.events;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import me.alexdevs.solstice.api.events.CommandEvents;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CommandDispatcher.class, remap = false)
public abstract class CommandEventsMixin<S> {
    // Lcom/mojang/brigadier/CommandDispatcher;parse(Lcom/mojang/brigadier/StringReader;Ljava/lang/Object;)Lcom/mojang/brigadier/ParseResults;

    @Inject(method = "parse(Lcom/mojang/brigadier/StringReader;Ljava/lang/Object;)Lcom/mojang/brigadier/ParseResults;", at = @At("HEAD"), cancellable = true)
    public void solstice$interceptCommands(StringReader reader, S sourceGeneric, CallbackInfoReturnable<ParseResults<S>> cir) {
        if (sourceGeneric instanceof CommandSourceStack source) {
            var command = reader.getString();
            if(command.startsWith("/"))
                return;

            if (!CommandEvents.ALLOW_COMMAND.invoker().allowCommand(source, command)) {
                cir.cancel();
            }

            CommandEvents.COMMAND.invoker().onCommand(source, command);
        }
    }

    // This isn't working anymore...?
    /*@Inject(method = "execute(Lcom/mojang/brigadier/ParseResults;)I", at = @At("HEAD"), remap = false)
    public void execute(ParseResults<S> parse, CallbackInfoReturnable<Integer> cir) throws CommandSyntaxException {
        var context = parse.getContext();
        if (context.getSource() instanceof CommandSourceStack source) {
            var command = parse.getReader().getString();
            if (!CommandEvents.ALLOW_COMMAND.invoker().allowCommand(source, command)) {
                cir.cancel();
            }

            CommandEvents.COMMAND.invoker().onCommand(source, command);
        }
    }*/
}
