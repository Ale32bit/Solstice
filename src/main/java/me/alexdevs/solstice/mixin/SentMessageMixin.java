package me.alexdevs.solstice.mixin;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.core.customFormats.CustomSentMessage;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SentMessage.class)
public interface SentMessageMixin {
    @Inject(method = "of", at = @At("HEAD"), cancellable = true)
    private static void solstice$of(SignedMessage message, CallbackInfoReturnable<SentMessage> cir) {
        if (message.isSenderMissing()) {
            cir.setReturnValue(new CustomSentMessage.Profileless(message.getContent()));
        } else {
            var sender = Solstice.server.getPlayerManager().getPlayer(message.getSender());
            cir.setReturnValue(new CustomSentMessage.Chat(message, sender));
        }
    }
}
