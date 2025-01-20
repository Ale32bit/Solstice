package me.alexdevs.solstice.mixin.modules.styling;

import me.alexdevs.solstice.modules.styling.CustomSentMessage;
import me.alexdevs.solstice.modules.styling.formatters.ConnectionActivityFormatter;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(PlayerManager.class)
public abstract class CustomConnectionMessagesMixin {
    @Unique
    private ServerPlayerEntity solstice$player = null;

    @Inject(method = "onPlayerConnect", at = @At("HEAD"))
    private void solstice$onJoin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        solstice$player = player;
    }

    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void solstice$onJoinReturn(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        solstice$player = null;
    }

    @ModifyArg(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"))
    public Text solstice$getPlayerJoinMessage(Text message) {
        var ogText = (TranslatableTextContent) message.getContent();
        var args = ogText.getArgs();

        if (args.length == 1) {
            return ConnectionActivityFormatter.onJoin(solstice$player);
        } else {
            return ConnectionActivityFormatter.onJoinRenamed(solstice$player, (String) args[1]);
        }
    }
}
