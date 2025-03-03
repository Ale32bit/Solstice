package me.alexdevs.solstice.mixin.modules.styling;

import com.llamalad7.mixinextras.sugar.Local;
import me.alexdevs.solstice.modules.styling.CustomSentMessage;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerList.class)
public abstract class CustomChatMessageMixin {
    @Redirect(
            method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/OutgoingChatMessage;create(Lnet/minecraft/network/chat/PlayerChatMessage;)Lnet/minecraft/network/chat/OutgoingChatMessage;")
    )
    private OutgoingChatMessage solstice$broadcast(PlayerChatMessage message, @Local(argsOnly = true) ServerPlayer sender) {
        return CustomSentMessage.of(message, sender);
    }
}
