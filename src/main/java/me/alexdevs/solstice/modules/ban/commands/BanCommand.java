package me.alexdevs.solstice.modules.ban.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.module.Utils;
import me.alexdevs.solstice.modules.ban.BanModule;
import me.alexdevs.solstice.modules.ban.formatters.BanMessageFormatter;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.players.UserBanListEntry;
import me.alexdevs.solstice.api.text.Format;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class BanCommand extends ModCommand<BanModule> {
    public static final SimpleCommandExceptionType ALREADY_BANNED_EXCEPTION = new SimpleCommandExceptionType(Component.translatable("commands.ban.failed"));

    public BanCommand(BanModule module) {
        super(module);
    }

    static int execute(CommandContext<CommandSourceStack> context, Collection<GameProfile> targets, @Nullable String reason, @Nullable Date expiryDate) throws CommandSyntaxException {
        var source = context.getSource();
        var server = source.getServer();
        var banList = server.getPlayerList().getBans();

        var banCounter = 0;
        for (GameProfile target : targets) {
            if (banList.isBanned(target)) {
                continue;
            }

            var banEntry = new UserBanListEntry(target, null, source.getTextName(), expiryDate, reason);
            banList.add(banEntry);
            banCounter++;

            var playerContext = PlaceholderContext.of(target, server);

            source.sendSuccess(() -> Component.translatable("commands.ban.success", Component.nullToEmpty(target.getName()), Format.parse(banEntry.getReason(), playerContext)), true);

            var serverPlayerEntity = source.getServer().getPlayerList().getPlayer(target.getId());
            if (serverPlayerEntity != null) {
                serverPlayerEntity.connection.disconnect(BanMessageFormatter.format(target, banEntry));
            }
        }

        if (banCounter == 0) {
            throw ALREADY_BANNED_EXCEPTION.create();
        } else {
            return banCounter;
        }
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandRegistry, Commands.CommandSelection environment) {
        Utils.removeCommands(dispatcher, "ban");
        super.register(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("ban");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(3))
                .then(argument("targets", GameProfileArgument.gameProfile())
                        .executes(context -> execute(context, GameProfileArgument.getGameProfiles(context, "targets"), null, null))
                        .then(argument("reason", StringArgumentType.greedyString())
                                .executes(context -> execute(context, GameProfileArgument.getGameProfiles(context, "targets"), StringArgumentType.getString(context, "reason"), null))));

    }
}
