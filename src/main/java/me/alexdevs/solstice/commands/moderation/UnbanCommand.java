package me.alexdevs.solstice.commands.moderation;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.alexdevs.solstice.commands.CommandInitializer;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.literal;

public class UnbanCommand {
    private static final SimpleCommandExceptionType ALREADY_UNBANNED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.pardon.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        CommandInitializer.removeCommands("pardon");

        var requirement = Permissions.require("solstice.command.unban", 3);

        var node = dispatcher.register(literal("unban")
                .requires(requirement)
                .then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile())
                        .suggests((context, builder) -> CommandSource.suggestMatching((context.getSource()).getServer().getPlayerManager().getUserBanList().getNames(), builder))
                        .executes(context -> execute(context, GameProfileArgumentType.getProfileArgument(context, "targets")))));

        dispatcher.register(literal("pardon").requires(requirement).redirect(node));
    }

    private static int execute(CommandContext<ServerCommandSource> context, Collection<GameProfile> targets) throws CommandSyntaxException {
        var banList = context.getSource().getServer().getPlayerManager().getUserBanList();
        var source = context.getSource();
        var pardonCount = 0;
        for(GameProfile profile : targets) {
            if(banList.contains(profile)) {
                banList.remove(profile);
                pardonCount++;
                source.sendFeedback(() -> Text.translatable("commands.pardon.success", Text.literal(profile.getName())), true);
            }
        }

        if (pardonCount == 0) {
            throw ALREADY_UNBANNED_EXCEPTION.create();
        } else {
            return pardonCount;
        }
    }
}
