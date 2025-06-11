package me.alexdevs.solstice.mixin.events;

import me.alexdevs.solstice.api.events.proxy.ProxyServerMessageEvents;
import me.alexdevs.solstice.api.events.proxy.ProxyServerPlayerEvents;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Inject(
            method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onSendChatMessage(
            PlayerChatMessage message,
            ServerPlayer sender,
            ChatType.Bound params,
            CallbackInfo ci
    ) {
        if (!ProxyServerMessageEvents.ALLOW_CHAT_MESSAGE.invoker().allowChatMessage(message, sender, params)) {
            ci.cancel();
            return;
        }

        ProxyServerMessageEvents.CHAT_MESSAGE.invoker().onChatMessage(message, sender, params);
    }

    @Inject(method = "respawn", at = @At("TAIL"))
    private void afterRespawn(
            ServerPlayer oldPlayer,
            boolean alive,
            Entity.RemovalReason removalReason,
            CallbackInfoReturnable<ServerPlayer> cir
    ) {
        ProxyServerPlayerEvents.AFTER_RESPAWN.invoker().afterRespawn(oldPlayer, cir.getReturnValue(), alive);
    }
}
