package me.alexdevs.solstice.mixin;

import me.alexdevs.solstice.core.customChat.CustomConnectionMessage;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Unique
    private ServerPlayerEntity solstice$player = null;

    @Inject(method="onPlayerConnect", at = @At("HEAD"))
    private void solstice$onJoin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        solstice$player = player;
    }

    @Inject(method="onPlayerConnect", at = @At("RETURN"))
    private void solstice$onJoinReturn(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        solstice$player = null;
    }

    @ModifyArg(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"))
    public Text solstice$getPlayerJoinMessage(Text message) {
        var ogText = (TranslatableTextContent) message.getContent();
        var args = ogText.getArgs();

        if (args.length == 1) {
            return CustomConnectionMessage.onJoin(solstice$player);
        } else {
            return CustomConnectionMessage.onJoinRenamed(solstice$player, (String) args[1]);
        }
    }
}
