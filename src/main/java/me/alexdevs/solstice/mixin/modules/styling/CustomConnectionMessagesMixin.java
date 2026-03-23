package me.alexdevs.solstice.mixin.modules.styling;

import me.alexdevs.solstice.modules.ModuleProvider;
import me.alexdevs.solstice.modules.styling.formatters.ConnectionActivityFormatter;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerPlayer;
//? if >= 1.21.1 {
/*import net.minecraft.server.network.CommonListenerCookie;
*///? }
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class CustomConnectionMessagesMixin {
    @Unique
    private ServerPlayer solstice$player = null;

    @Inject(method = "placeNewPlayer", at = @At("HEAD"))
    //? if >= 1.21.1 {
    /*private void solstice$onJoin(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
    *///? } else {
    private void solstice$onJoin(Connection connection, ServerPlayer player, CallbackInfo ci) {
    //? }
        solstice$player = player;
    }

    @Inject(method = "placeNewPlayer", at = @At("RETURN"))
    //? if >= 1.21.1 {
    /*private void solstice$onJoinReturn(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
    *///? } else {
    private void solstice$onJoinReturn(Connection connection, ServerPlayer player, CallbackInfo ci) {
    //? }
        solstice$player = null;
    }

    @Redirect(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    private void solstice$sendJoinMessage(PlayerList list, Component message, boolean bypassHiddenChat) {
        var ogText = (TranslatableContents) message.getContents();
        var args = ogText.getArgs();

        Component formattedMessage;
        if (args.length == 1) {
            formattedMessage = ConnectionActivityFormatter.onJoin(solstice$player);
        } else {
            formattedMessage = ConnectionActivityFormatter.onJoinRenamed(solstice$player, (String) args[1]);
        }

        ModuleProvider.STYLING.broadcastActivity(list, formattedMessage, bypassHiddenChat);
    }
}
