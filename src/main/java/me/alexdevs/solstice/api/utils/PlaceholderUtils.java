package me.alexdevs.solstice.api.utils;

import com.mojang.authlib.GameProfile;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class PlaceholderUtils {
    private PlaceholderUtils() {
    }

    public static PlaceholderContext of(CommandSourceStack source) {
        //? if >= 26.1 {
        /*var entity = source.getEntity();
        return entity != null ? PlaceholderContext.of(entity) : PlaceholderContext.of(source.getLevel());
        *///? } elif >= 1.21.1 {
        return PlaceholderContext.of(source);
        //? }
    }

    public static PlaceholderContext of(MinecraftServer server) {
        //? if >= 26.1 {
        /*// fallback to a serverlevel since latest versions don't have PlaceholderContext.of(MinecraftServer)
        return PlaceholderContext.of(server.overworld()); //hopefully this works fine
        *///? } elif < 26.1 {
        return PlaceholderContext.of(server);
        //? }
     }

    public static PlaceholderContext of(GameProfile profile, MinecraftServer server) {
        //? if >= 26.1 {
        /*// fallback to a serverlevel since latest versions don't have PlaceholderContext.of(GameProfile,MinecraftServer)
        var player = server.getPlayerList().getPlayer(PlayerUtils.getId(profile));
        if (player != null) {
            return PlaceholderContext.of(player); // if we have an online player
        }
        else {
            return of(server); // otherwise fallback to server
        }
        *///? } elif < 26.1 {
        return PlaceholderContext.of(profile, server);
        //? }
    }

    public static PlaceholderContext of(Player player) {
        return PlaceholderContext.of(player);
    }
}
