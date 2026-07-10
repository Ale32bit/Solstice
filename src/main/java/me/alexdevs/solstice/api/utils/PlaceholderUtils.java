package me.alexdevs.solstice.api.utils;

import com.mojang.authlib.GameProfile;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;

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
        //? if >= 26.1
        //return PlaceholderContext.of();
        //? if < 26.1
        return PlaceholderContext.of(server);
    }

    public static PlaceholderContext of(GameProfile profile, MinecraftServer server) {
        //? if >= 26.1
        //return PlaceholderContext.of();
        //? if < 26.1
        return PlaceholderContext.of(profile, server);
    }
}
