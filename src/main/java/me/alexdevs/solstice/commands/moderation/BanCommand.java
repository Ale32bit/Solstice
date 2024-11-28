package me.alexdevs.solstice.commands.moderation;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.commands.CommandInitializer;
import me.alexdevs.solstice.core.customFormats.CustomBanMessage;
import me.alexdevs.solstice.util.Format;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Date;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BanCommand {
    public static final SimpleCommandExceptionType ALREADY_BANNED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.ban.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        CommandInitializer.removeCommands("ban");

        var rootCommand = literal("ban")
                .requires(Permissions.require("solstice.command.ban", 3))
                .then(argument("targets", GameProfileArgumentType.gameProfile())
                .executes(context -> execute(context, GameProfileArgumentType.getProfileArgument(context, "targets"), null, null))
                        .then(argument("reason", StringArgumentType.greedyString())
                                .executes(context -> execute(context, GameProfileArgumentType.getProfileArgument(context, "targets"), StringArgumentType.getString(context, "reason"), null))));

        dispatcher.register(rootCommand);
    }

    static int execute(CommandContext<ServerCommandSource> context, Collection<GameProfile> targets, @Nullable String reason, @Nullable Date expiryDate) throws CommandSyntaxException {
        var source = context.getSource();
        var server = source.getServer();
        var banList = server.getPlayerManager().getUserBanList();

        var banCounter = 0;
        for (GameProfile target : targets) {
            if (banList.contains(target)) {
                continue;
            }

            var banEntry = new BannedPlayerEntry(target, null, source.getName(), expiryDate, reason);
            banList.add(banEntry);
            banCounter++;

            var playerContext = PlaceholderContext.of(target, server);

            source.sendFeedback(() -> Text.translatable("commands.ban.success", Texts.toText(target), Format.parse(banEntry.getReason(), playerContext)), true);

            var serverPlayerEntity = source.getServer().getPlayerManager().getPlayer(target.getId());
            if (serverPlayerEntity != null) {
                serverPlayerEntity.networkHandler.disconnect(CustomBanMessage.format(target, banEntry));
            }
        }

        if (banCounter == 0) {
            throw ALREADY_BANNED_EXCEPTION.create();
        } else {
            return banCounter;
        }
    }
}
