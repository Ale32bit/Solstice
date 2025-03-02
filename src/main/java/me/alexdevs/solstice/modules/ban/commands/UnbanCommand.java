package me.alexdevs.solstice.modules.ban.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.module.Utils;
import me.alexdevs.solstice.modules.ban.BanModule;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import java.util.Collection;
import java.util.List;

import static net.minecraft.commands.Commands.literal;

public class UnbanCommand extends ModCommand<BanModule> {
    private static final SimpleCommandExceptionType ALREADY_UNBANNED_EXCEPTION = new SimpleCommandExceptionType(Component.translatable("commands.pardon.failed"));

    public UnbanCommand(BanModule module) {
        super(module);
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandRegistry, Commands.CommandSelection environment) {
        Utils.removeCommands(dispatcher, "pardon");
        super.register(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("unban", "pardon");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(3))
                .then(Commands.argument("targets", GameProfileArgument.gameProfile())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest((context.getSource()).getServer().getPlayerList().getBans().getUserList(), builder))
                        .executes(context -> execute(context, GameProfileArgument.getGameProfiles(context, "targets"))));
    }

    private int execute(CommandContext<CommandSourceStack> context, Collection<GameProfile> targets) throws CommandSyntaxException {
        var banList = context.getSource().getServer().getPlayerList().getBans();
        var source = context.getSource();
        var pardonCount = 0;
        for (GameProfile profile : targets) {
            if (banList.isBanned(profile)) {
                banList.remove(profile);
                pardonCount++;
                source.sendSuccess(() -> Component.translatable("commands.pardon.success", ComponentUtils.getDisplayName(profile)), true);
            }
        }

        if (pardonCount == 0) {
            throw ALREADY_UNBANNED_EXCEPTION.create();
        } else {
            return pardonCount;
        }
    }
}
