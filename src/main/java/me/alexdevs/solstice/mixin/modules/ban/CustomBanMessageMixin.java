package me.alexdevs.solstice.mixin.modules.ban;

import com.llamalad7.mixinextras.sugar.Local;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.utils.ProfileOrNameAndId;
import me.alexdevs.solstice.modules.ban.formatters.BanMessageFormatter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.UserBanListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;

@Mixin(PlayerList.class)
public abstract class CustomBanMessageMixin {
    @Inject(method = "canPlayerLogin", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    //? >= 1.21.11
    //public void solstice$formatBanMessage(SocketAddress address, net.minecraft.server.players.NameAndId profile, CallbackInfoReturnable<Component> cir, @Local UserBanListEntry bannedPlayerEntry, @Local MutableComponent mutableText) {
    //? < 1.21.11
    public void solstice$formatBanMessage(SocketAddress address, com.mojang.authlib.GameProfile profile, CallbackInfoReturnable<Component> cir, @Local UserBanListEntry bannedPlayerEntry, @Local MutableComponent mutableText) {
        try {
            var reasonText = BanMessageFormatter.format(new ProfileOrNameAndId(profile), bannedPlayerEntry);
            cir.setReturnValue(reasonText);
        } catch (Exception ex) {
            Solstice.LOGGER.error("Something went wrong while formatting the ban message", ex);

            // Ensure the original text message is returned to avoid exploits and bypass the ban
            cir.setReturnValue(mutableText);
        }
    }
}
