package me.alexdevs.solstice.mixin.modules.styling;

import me.alexdevs.solstice.modules.styling.StylingModule;
import me.alexdevs.solstice.modules.styling.formatters.ConnectionActivityFormatter;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class PlayerDisconnectMixin {
    @Shadow
    public ServerPlayer player;

    @Redirect(method = "removePlayerFromWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    private void solstice$sendLeaveMessage(PlayerList playerList, Component message, boolean bypassHiddenChat) {
        var formattedMessage = ConnectionActivityFormatter.onLeave(this.player);
        StylingModule.broadcastActivity(playerList, formattedMessage, bypassHiddenChat);
    }
}
