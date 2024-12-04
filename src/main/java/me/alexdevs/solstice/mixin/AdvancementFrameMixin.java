package me.alexdevs.solstice.mixin;

import me.alexdevs.solstice.core.customFormats.CustomAdvancementMessage;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementFrame.class)
public abstract class AdvancementFrameMixin {
    // MutableText
    @Inject(method = "getChatAnnouncementText", at = @At("HEAD"), cancellable = true)
    public void solstice$getCustomAnnouncement(AdvancementEntry entry, ServerPlayerEntity player, CallbackInfoReturnable<MutableText> cir) {
        cir.setReturnValue(CustomAdvancementMessage.getText(player, entry, (AdvancementFrame) (Object) this).copy());
    }

    /*@Shadow
    private ServerPlayerEntity owner;

    @ModifyArg(method = "grantCriterion", at = @At(value = "INVOKE", target = ""))
    public Text solstice$customAdvancement(Text message) {
        try {
            var translatable = (TranslatableTextContent) message.getContent();
            var key = translatable.getKey();
            var frameId = key.replace("chat.type.advancement.", "");
            var advancementContent = (TranslatableTextContent) ((MutableText) translatable.getArg(1)).getContent();
            var advancementKey = ((TranslatableTextContent) ((MutableText) advancementContent.getArg(0)).getContent()).getKey().replace(".title", "");
            return CustomAdvancementMessage.getText(owner, advancementKey, frameId);
        } catch(Exception e) {
            Solstice.LOGGER.error("Exception customizing advancement message", e);

            return message;
        }
    }*/
}
