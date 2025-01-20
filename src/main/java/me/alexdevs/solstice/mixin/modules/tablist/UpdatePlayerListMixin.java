package me.alexdevs.solstice.mixin.modules.tablist;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.tablist.data.TabListConfig;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;
import java.util.List;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class UpdatePlayerListMixin {
    @Shadow
    public ServerPlayerEntity player;
    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "tick", at = @At("TAIL"))
    private void solstice$updatePlayerList(CallbackInfo ci) {
        if (Solstice.configManager.getData(TabListConfig.class).enable) {
            var packet = new PlayerListS2CPacket(EnumSet.of(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, PlayerListS2CPacket.Action.UPDATE_LISTED), List.of(this.player));
            this.server.getPlayerManager().sendToAll(packet);
        }
    }
}
