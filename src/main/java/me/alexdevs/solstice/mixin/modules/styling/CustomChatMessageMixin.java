package me.alexdevs.solstice.mixin.modules.styling;

import me.alexdevs.solstice.modules.styling.CustomSentMessage;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(PlayerManager.class)
public abstract class CustomChatMessageMixin {
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
