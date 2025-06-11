package me.alexdevs.solstice.neoforge.mixin;

import me.alexdevs.solstice.api.events.proxy.ProxyEntitySleepEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Redirect(
            method = "lambda$startSleepInBed$13",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isDay()Z")
    )
    private boolean redirectDaySleepCheck(Level world, BlockPos pos) {
        boolean day = world.isDay();

        InteractionResult result = ProxyEntitySleepEvents.ALLOW_SLEEP_TIME.invoker()
                .allowSleepTime((Player) (Object) this, pos, !day);

        if (result != InteractionResult.PASS) {
            return !result.consumesAction(); // true from the event = night-like conditions, so we have to invert
        }

        return day;
    }
}
