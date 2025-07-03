package me.alexdevs.solstice.mixin.modules.sign;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.ModuleProvider;
import me.alexdevs.solstice.modules.sign.SignModule;
import net.minecraft.server.network.FilteredText;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(SignBlockEntity.class)
public abstract class FormatSignMixin {

    @Inject(method = "setMessages", at = @At("HEAD"), cancellable = true)
    private void solstice$formatSignText(Player player, List<FilteredText> messages, SignText text, CallbackInfoReturnable<SignText> cir) {
        if (ModuleProvider.SIGN.canFormatSign(player)) {
            try {
                text = SignModule.formatSign(messages, text);
                cir.setReturnValue(text);
            } catch (Exception e) {
                Solstice.LOGGER.error("Something went wrong while formatting a sign!", e);
            }
        }
    }
}
