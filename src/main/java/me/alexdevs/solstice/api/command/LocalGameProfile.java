package me.alexdevs.solstice.api.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.alexdevs.solstice.Solstice;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import java.util.concurrent.CompletableFuture;

public class LocalGameProfile {
//    public static GameProfile getGameProfile(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
//        var profiles = GameProfileArgument.getGameProfiles(context, name);
//        if (profiles.size() > 1) {
//            throw EntityArgument.ERROR_NOT_SINGLE_PLAYER.create();
//        }
//
//        return profiles.iterator().next();
//    }

    public static com.mojang.authlib.GameProfile getProfile(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        var profileName = StringArgumentType.getString(context, name);
        var profile = Solstice.getUserCache().getByName(profileName);
        if(profile.isEmpty())
            throw GameProfileArgument.ERROR_UNKNOWN_PLAYER.create();

        return profile.get();
    }

    /**
     * Suggest player name. Prefer online players, then cached.
     * @param context
     * @param builder
     * @return
     */
    public static CompletableFuture<Suggestions> suggest(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        var server = context.getSource().getServer();
        var onlinePlayers = server.getPlayerList().getPlayerNamesArray();

        var input = builder.getRemainingLowerCase();
        for (var player : onlinePlayers) {
            if (SharedSuggestionProvider.matchesSubStr(input, player.toLowerCase())) {
                return SharedSuggestionProvider.suggest(onlinePlayers, builder);
            }
        }

        var cachedPlayers = Solstice.getUserCache().getAllNames();
        return SharedSuggestionProvider.suggest(cachedPlayers, builder);
    }
}
