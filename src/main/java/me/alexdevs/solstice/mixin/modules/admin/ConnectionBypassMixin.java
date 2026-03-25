package me.alexdevs.solstice.mixin.modules.admin;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.PlayerConnectionEvents;
import me.alexdevs.solstice.api.utils.PlayerUtils;
import me.alexdevs.solstice.api.utils.ProfileOrNameAndId;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedPlayerList.class)
public abstract class ConnectionBypassMixin {
    @Inject(method = "isWhiteListed", at = @At("HEAD"), cancellable = true)
    //? if < 1.21.11
    public void solstice$bypassWhitelist(com.mojang.authlib.GameProfile profile, CallbackInfoReturnable<Boolean> cir) {
    //? if >= 1.21.11
    //public void solstice$bypassWhitelist(net.minecraft.server.players.NameAndId profile, CallbackInfoReturnable<Boolean> cir) {
        try {
            if (PlayerConnectionEvents.WHITELIST_BYPASS.invoker().bypassWhitelist(new ProfileOrNameAndId(profile).getProfile()))
                cir.setReturnValue(true);
        } catch (Exception e) {
            Solstice.LOGGER.error("Error checking whitelist bypass for profile {}", PlayerUtils.getId(profile), e);
        }
    }

    @Inject(method = "canBypassPlayerLimit", at = @At("HEAD"), cancellable = true)
    //? if < 1.21.11
    public void solstice$bypassPlayerLimit(com.mojang.authlib.GameProfile profile, CallbackInfoReturnable<Boolean> cir) {
    //? if >= 1.21.11
    //public void solstice$bypassPlayerLimit(net.minecraft.server.players.NameAndId profile, CallbackInfoReturnable<Boolean> cir) {
        try {
            if (PlayerConnectionEvents.FULL_SERVER_BYPASS.invoker().bypassFullServer(new ProfileOrNameAndId(profile).getProfile()))
                cir.setReturnValue(true);
        } catch (Exception e) {
            Solstice.LOGGER.error("Error checking full server bypass for profile {}", PlayerUtils.getId(profile), e);
        }
    }
}
