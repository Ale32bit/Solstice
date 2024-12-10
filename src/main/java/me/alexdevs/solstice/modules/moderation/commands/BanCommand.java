package me.alexdevs.solstice.modules.moderation.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.styling.formatters.BanMessageFormatter;
import me.alexdevs.solstice.modules.Utils;
import me.alexdevs.solstice.util.Format;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BanCommand extends ModCommand {
    public static final SimpleCommandExceptionType ALREADY_BANNED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.ban.failed"));

    public BanCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public void register() {
        Utils.removeCommands(this.dispatcher, "ban");
        super.register();
    }

    @Override
    public List<String> getNames() {
        return List.of("ban");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(3))
                .then(argument("targets", GameProfileArgumentType.gameProfile())
                        .executes(context -> execute(context, GameProfileArgumentType.getProfileArgument(context, "targets"), null, null))
                        .then(argument("reason", StringArgumentType.greedyString())
                                .executes(context -> execute(context, GameProfileArgumentType.getProfileArgument(context, "targets"), StringArgumentType.getString(context, "reason"), null))));

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
                serverPlayerEntity.networkHandler.disconnect(BanMessageFormatter.format(target, banEntry));
            }
        }

        if (banCounter == 0) {
            throw ALREADY_BANNED_EXCEPTION.create();
        } else {
            return banCounter;
        }
    }
}
