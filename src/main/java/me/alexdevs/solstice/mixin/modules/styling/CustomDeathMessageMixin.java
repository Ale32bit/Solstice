package me.alexdevs.solstice.mixin.modules.styling;

import me.alexdevs.solstice.modules.styling.formatters.DeathFormatter;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerEntity.class)
public abstract class CustomDeathMessageMixin {
    @Redirect(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageTracker;getDeathMessage()Lnet/minecraft/text/Text;"))
    private Text solstice$getDeathMessage(DamageTracker instance) {
        var player = (ServerPlayerEntity) (Object) this;
        return DeathFormatter.onDeath(player, instance);
    }
}
