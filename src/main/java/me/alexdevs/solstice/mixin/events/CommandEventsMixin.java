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

    @Inject(method = "parse(Lcom/mojang/brigadier/StringReader;Ljava/lang/Object;)Lcom/mojang/brigadier/ParseResults;", at = @At("HEAD"), cancellable = true)
    public void solstice$interceptCommands(StringReader reader, S sourceGeneric, CallbackInfoReturnable<ParseResults<S>> cir) {
        if (sourceGeneric instanceof CommandSourceStack source) {
            var command = reader.getString();
            //? if < 1.21.1 {
            /*if(command.startsWith("/"))
                return;
            *///? }
            if (!CommandEvents.ALLOW_COMMAND.invoker().allowCommand(source, command)) {
                cir.cancel();
            }

            CommandEvents.COMMAND.invoker().onCommand(source, command);
        }
    }
}
