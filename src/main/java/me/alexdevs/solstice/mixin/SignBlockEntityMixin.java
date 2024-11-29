package me.alexdevs.solstice.mixin;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.core.FormattableSigns;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.filter.FilteredMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(SignBlockEntity.class)
public abstract class SignBlockEntityMixin {

    @Inject(method = "getTextWithMessages", at = @At("HEAD"), cancellable = true)
    private void getTextWithMessages(PlayerEntity player, List<FilteredMessage> messages, SignText text, CallbackInfoReturnable<SignText> cir) {
        if(FormattableSigns.canFormatSign(player)) {
            try {
                text = FormattableSigns.formatSign(messages, text);
                cir.setReturnValue(text);
            } catch(Exception e) {
                Solstice.LOGGER.error("Something went wrong while formatting a sign!", e);
            }
        }
    }
}
