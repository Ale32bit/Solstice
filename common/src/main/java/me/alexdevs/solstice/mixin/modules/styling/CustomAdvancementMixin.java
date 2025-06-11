package me.alexdevs.solstice.mixin.modules.styling;

import me.alexdevs.solstice.modules.styling.formatters.AdvancementFormatter;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementType.class)
public abstract class CustomAdvancementMixin {

    @Inject(method = "createAnnouncement", at = @At("HEAD"), cancellable = true)
    public void solstice$getCustomAnnouncement(AdvancementHolder advancement, ServerPlayer player, CallbackInfoReturnable<MutableComponent> cir) {
        cir.setReturnValue(AdvancementFormatter.getText(player, advancement, (AdvancementType) (Object) this).copy());
    }
}
