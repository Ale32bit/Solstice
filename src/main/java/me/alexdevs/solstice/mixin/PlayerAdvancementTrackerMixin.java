package me.alexdevs.solstice.mixin;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.core.customChat.CustomAdvancementMessage;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PlayerAdvancementTracker.class)
public abstract class PlayerAdvancementTrackerMixin {
    @Shadow
    private ServerPlayerEntity owner;

    @ModifyArg(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"))
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
    }
}
