package me.alexdevs.solstice.api.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.alexdevs.solstice.Solstice;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class LocalGameProfile {
    public static GameProfile getGameProfile(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        var profiles = GameProfileArgumentType.getProfileArgument(context, name);
        if (profiles.size() > 1) {
            throw EntityArgumentType.TOO_MANY_PLAYERS_EXCEPTION.create();
        }

        return profiles.iterator().next();
    }

    public static GameProfile getProfile(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        var profileName = StringArgumentType.getString(context, name);
        var profile = Solstice.getUserCache().getByName(profileName);
        if(profile.isEmpty())
            throw GameProfileArgumentType.UNKNOWN_PLAYER_EXCEPTION.create();

        return profile.get();
    }

    /**
     * Suggest player name. Prefer online players, then cached.
     * @param context
     * @param builder
     * @return
     */
    public static CompletableFuture<Suggestions> suggest(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        var server = context.getSource().getServer();
        var onlinePlayers = server.getPlayerManager().getPlayerNames();

        var input = builder.getRemainingLowerCase();
        for (var player : onlinePlayers) {
            if (CommandSource.shouldSuggest(input, player.toLowerCase())) {
                return CommandSource.suggestMatching(onlinePlayers, builder);
            }
        }

        var cachedPlayers = Solstice.getUserCache().getAllNames();
        return CommandSource.suggestMatching(cachedPlayers, builder);
    }
}
