package me.alexdevs.solstice.mixin.modules.admin;

import com.mojang.authlib.GameProfile;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.PlayerConnectionEvents;
import net.minecraft.server.dedicated.DedicatedPlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedPlayerManager.class)
public abstract class ConnectionBypassMixin {
    @Inject(method = "isWhitelisted", at = @At("HEAD"), cancellable = true)
    public void solstice$bypassWhitelist(GameProfile profile, CallbackInfoReturnable<Boolean> cir) {
        try {
            if (PlayerConnectionEvents.WHITELIST_BYPASS.invoker().bypassWhitelist(profile))
                cir.setReturnValue(true);
        } catch (Exception e) {
            Solstice.LOGGER.error("Error checking whitelist bypass for profile {}", profile.getId(), e);
        }
    }

    @Inject(method = "canBypassPlayerLimit", at = @At("HEAD"), cancellable = true)
    public void solstice$bypassPlayerLimit(GameProfile profile, CallbackInfoReturnable<Boolean> cir) {
        try {
            if (PlayerConnectionEvents.FULL_SERVER_BYPASS.invoker().bypassFullServer(profile))
                cir.setReturnValue(true);
        } catch (Exception e) {
            Solstice.LOGGER.error("Error checking full server bypass for profile {}", profile.getId(), e);
        }
    }
}
