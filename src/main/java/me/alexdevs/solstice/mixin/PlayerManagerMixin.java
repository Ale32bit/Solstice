package me.alexdevs.solstice.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.core.customFormats.CustomBanMessage;
import me.alexdevs.solstice.core.customFormats.CustomConnectionMessage;
import me.alexdevs.solstice.core.customFormats.CustomSentMessage;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;
import java.util.function.Predicate;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
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
            return CustomConnectionMessage.onJoin(solstice$player);
        } else {
            return CustomConnectionMessage.onJoinRenamed(solstice$player, (String) args[1]);
        }
    }

    @Inject(method = "checkCanJoin", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    public void solstice$formatBanMessage(SocketAddress address, GameProfile profile, CallbackInfoReturnable<Text> cir, @Local BannedPlayerEntry bannedPlayerEntry, @Local MutableText mutableText) {
        try {
            var reasonText = CustomBanMessage.format(profile, bannedPlayerEntry);
            cir.setReturnValue(reasonText);
        } catch (Exception ex) {
            Solstice.LOGGER.error("Something went wrong while formatting the ban message", ex);

            // Ensure the original text message is returned to avoid exploits and bypass the ban
            cir.setReturnValue(mutableText);
        }
    }

    @Redirect(
            method = "broadcast(Lnet/minecraft/network/message/SignedMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/message/MessageType$Parameters;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/message/SentMessage;of(Lnet/minecraft/network/message/SignedMessage;)Lnet/minecraft/network/message/SentMessage;")
    )
    private SentMessage solstice$broadcast(SignedMessage sentMessagePar, SignedMessage message, Predicate<ServerPlayerEntity> shouldSendFiltered, @Nullable ServerPlayerEntity sender, MessageType.Parameters params) {
        return CustomSentMessage.of(message, sender);
    }
}
