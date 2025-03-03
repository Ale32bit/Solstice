package me.alexdevs.solstice.mixin.modules.styling;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.styling.formatters.AdvancementFormatter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PlayerAdvancements.class)
public abstract class CustomAdvancementMixin {
    @Shadow
    private ServerPlayer player;

    @ModifyArg(method = "award", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    public Component solstice$customAdvancement(Component message) {
        try {
            var translatable = (TranslatableContents) message.getContents();
            var key = translatable.getKey();
            var frameId = key.replace("chat.type.advancement.", "");
            var advancementContent = (TranslatableContents) ((MutableComponent) translatable.getArgument(1)).getContents();
            var advancementKey = ((TranslatableContents) ((MutableComponent) advancementContent.getArgument(0)).getContents()).getKey().replace(".title", "");
            return AdvancementFormatter.getText(player, advancementKey, frameId);
        } catch (Exception e) {
            Solstice.LOGGER.error("Exception customizing advancement message", e);

            return message;
        }
    }
}
