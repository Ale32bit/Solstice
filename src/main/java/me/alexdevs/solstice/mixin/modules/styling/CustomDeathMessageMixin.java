package me.alexdevs.solstice.mixin.modules.styling;

import me.alexdevs.solstice.modules.styling.formatters.DeathFormatter;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.CombatTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayer.class)
public abstract class CustomDeathMessageMixin {
    @Redirect(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/CombatTracker;getDeathMessage()Lnet/minecraft/network/chat/Component;"))
    private Component solstice$getDeathMessage(CombatTracker instance) {
        var player = (ServerPlayer) (Object) this;
        return DeathFormatter.onDeath(player, instance);
    }
}
