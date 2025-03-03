package me.alexdevs.solstice.mixin.modules.tablist;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.tablist.data.TabListConfig;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;
import java.util.List;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class UpdatePlayerListMixin {
    @Shadow
    public ServerPlayer player;
    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "tick", at = @At("TAIL"))
    private void solstice$updatePlayerList(CallbackInfo ci) {
        if (Solstice.configManager.getData(TabListConfig.class).enable) {
            var packet = new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME, ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED), List.of(this.player));
            this.server.getPlayerList().broadcastAll(packet);
        }
    }
}
