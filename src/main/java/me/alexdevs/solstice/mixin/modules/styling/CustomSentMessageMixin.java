package me.alexdevs.solstice.mixin.modules.styling;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.styling.CustomSentMessage;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OutgoingChatMessage.class)
public interface CustomSentMessageMixin {
    @Inject(method = "create", at = @At("HEAD"), cancellable = true)
    private static void solstice$of(PlayerChatMessage message, CallbackInfoReturnable<OutgoingChatMessage> cir) {
        if (message.isSystem()) {
            cir.setReturnValue(new CustomSentMessage.Profileless(message.decoratedContent()));
        } else {
            var sender = Solstice.server.getPlayerList().getPlayer(message.sender());
            cir.setReturnValue(new CustomSentMessage.Chat(message, sender));
        }
    }
}
