package me.alexdevs.solstice.modules.moderation.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.Utils;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

import java.util.Collection;
import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class UnbanCommand extends ModCommand {
    private static final SimpleCommandExceptionType ALREADY_UNBANNED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.pardon.failed"));

    public UnbanCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public void register() {
        Utils.removeCommands(dispatcher, "pardon");
        super.register();
    }

    @Override
    public List<String> getNames() {
        return List.of("unban", "pardon");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(3))
                .then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile())
                        .suggests((context, builder) -> CommandSource.suggestMatching((context.getSource()).getServer().getPlayerManager().getUserBanList().getNames(), builder))
                        .executes(context -> execute(context, GameProfileArgumentType.getProfileArgument(context, "targets"))));
    }

    private int execute(CommandContext<ServerCommandSource> context, Collection<GameProfile> targets) throws CommandSyntaxException {
        var banList = context.getSource().getServer().getPlayerManager().getUserBanList();
        var source = context.getSource();
        var pardonCount = 0;
        for (GameProfile profile : targets) {
            if (banList.contains(profile)) {
                banList.remove(profile);
                pardonCount++;
                source.sendFeedback(() -> Text.translatable("commands.pardon.success", Text.of(profile.getName())), true);
            }
        }

        if (pardonCount == 0) {
            throw ALREADY_UNBANNED_EXCEPTION.create();
        } else {
            return pardonCount;
        }
    }
}
