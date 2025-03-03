package me.alexdevs.solstice.mixin.modules.styling;

import me.alexdevs.solstice.modules.styling.CustomSentMessage;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(PlayerList.class)
public abstract class CustomChatMessageMixin {
    @Redirect(
            method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/OutgoingChatMessage;create(Lnet/minecraft/network/chat/PlayerChatMessage;)Lnet/minecraft/network/chat/OutgoingChatMessage;")
    )
    private OutgoingChatMessage solstice$broadcast(PlayerChatMessage sentMessagePar, PlayerChatMessage message, Predicate<ServerPlayer> shouldSendFiltered, @Nullable ServerPlayer sender, ChatType.Bound params) {
        return CustomSentMessage.of(message, sender);
    }
}
