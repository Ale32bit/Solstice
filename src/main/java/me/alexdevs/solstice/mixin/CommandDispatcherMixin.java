package me.alexdevs.solstice.mixin;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandDispatcher.class)
public class CommandDispatcherMixin<S> {
    @Inject(method = "execute(Lcom/mojang/brigadier/ParseResults;)I", at = @At("HEAD"), remap = false)
    public void execute(final ParseResults<S> parse, CallbackInfoReturnable<Integer> cir) {
        var context = parse.getContext();
        if (context.getSource() instanceof ServerCommandSource source) {
            if(source.isExecutedByPlayer()) {
                var player = source.getPlayer();
                if(player != null) {
                    var command = parse.getReader().getString();
                    Solstice.LOGGER.info("{}: /{}", player.getGameProfile().getName(), command);
                    try {
                        SolsticeEvents.PLAYER_COMMAND.invoker().onPlayerCommand(player, command);
                    } catch (Exception e) {
                        Solstice.LOGGER.error("Error in CommandDispatcher mixin", e);
                    }
                }
            }
        }
    }
}
