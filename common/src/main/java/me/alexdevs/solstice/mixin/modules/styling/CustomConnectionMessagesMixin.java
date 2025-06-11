package me.alexdevs.solstice.mixin.modules.styling;

import me.alexdevs.solstice.modules.styling.formatters.ConnectionActivityFormatter;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class CustomConnectionMessagesMixin {
    @Unique
    private ServerPlayer solstice$player = null;

    @Inject(method = "placeNewPlayer", at = @At("HEAD"))
    private void solstice$onJoin(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
        solstice$player = player;
    }

    @Inject(method = "placeNewPlayer", at = @At("RETURN"))
    private void solstice$onJoinReturn(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
        solstice$player = null;
    }

    @ModifyArg(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    public Component solstice$getPlayerJoinMessage(Component message) {
        var ogText = (TranslatableContents) message.getContents();
        var args = ogText.getArgs();

        if (args.length == 1) {
            return ConnectionActivityFormatter.onJoin(solstice$player);
        } else {
            return ConnectionActivityFormatter.onJoinRenamed(solstice$player, (String) args[1]);
        }
    }
}
