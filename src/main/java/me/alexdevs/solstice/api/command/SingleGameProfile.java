package me.alexdevs.solstice.api.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;

public class SingleGameProfile {
    public static GameProfile getProfile(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        var profiles = GameProfileArgumentType.getProfileArgument(context, name);
        if (profiles.size() > 1) {
            throw EntityArgumentType.TOO_MANY_PLAYERS_EXCEPTION.create();
        }

        return profiles.iterator().next();
    }
}
