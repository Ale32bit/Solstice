package me.alexdevs.solstice.mixin;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.WorldSave;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "save", at = @At("TAIL"))
    public void save(boolean suppressLogs, boolean flush, boolean force, CallbackInfoReturnable<Boolean> cir) {
        try {
            WorldSave.EVENT.invoker().onSave((MinecraftServer) (Object) this, suppressLogs, flush, force);
        } catch(Exception e) {
            Solstice.LOGGER.error("Exception emitting world save event", e);
        }
    }
}
