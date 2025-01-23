package me.alexdevs.solstice.mixin.modules.ban;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.ban.formatters.BanMessageFormatter;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;

@Mixin(PlayerManager.class)
public abstract class CustomBanMessageMixin {
    @Inject(method = "checkCanJoin", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    public void solstice$formatBanMessage(SocketAddress address, GameProfile profile, CallbackInfoReturnable<Text> cir, @Local BannedPlayerEntry bannedPlayerEntry, @Local MutableText mutableText) {
        try {
            var reasonText = BanMessageFormatter.format(profile, bannedPlayerEntry);
            cir.setReturnValue(reasonText);
        } catch (Exception ex) {
            Solstice.LOGGER.error("Something went wrong while formatting the ban message", ex);

            // Ensure the original text message is returned to avoid exploits and bypass the ban
            cir.setReturnValue(mutableText);
        }
    }
}
