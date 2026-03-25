package me.alexdevs.solstice.mixin.modules.styling;
import me.alexdevs.solstice.modules.styling.formatters.AdvancementFormatter;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

//? if >= 1.21.1 {
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
//? } else {
/*import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.Component;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
*///? }


//? >= 1.21.1
@Mixin(AdvancementType.class)
//? < 1.21.1
//@Mixin(PlayerAdvancements.class)
public abstract class CustomAdvancementMixin {
    //? if >= 1.21.1 {
    @Inject(method = "createAnnouncement", at = @At("HEAD"), cancellable = true)
    public void solstice$getCustomAnnouncement(AdvancementHolder advancement, ServerPlayer player, CallbackInfoReturnable<MutableComponent> cir) {
        cir.setReturnValue(AdvancementFormatter.getText(player, advancement, (AdvancementType) (Object) this).copy());
    }
    //? } else {
    /*@Shadow
    private ServerPlayer player;
    @Final
    @Shadow
    private PlayerList playerList;

    @Inject(method = "award", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void broadcastAdvancementMessage(Advancement advancement, String criterionKey, CallbackInfoReturnable<Boolean> cir) {
        this.playerList.broadcastSystemMessage(AdvancementFormatter.getText(this.player, advancement), false);
    }
    @Redirect(method = "award", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    public void broadcastSystemMessageRedirect(PlayerList instance, Component message, boolean bypassHiddenChat) {
    }
    *///? }
}
