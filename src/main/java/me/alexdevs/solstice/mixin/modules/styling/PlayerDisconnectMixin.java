package me.alexdevs.solstice.mixin.modules.styling;

import me.alexdevs.solstice.modules.styling.formatters.ConnectionActivityFormatter;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class PlayerDisconnectMixin {
    @Shadow
    public ServerPlayerEntity player;

    @ModifyArg(method = "cleanUp", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"))
    private Text solstice$getPlayerLeaveMessage(Text message) {
        return ConnectionActivityFormatter.onLeave(this.player);
    }
}
