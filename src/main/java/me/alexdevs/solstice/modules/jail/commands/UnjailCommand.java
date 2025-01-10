package me.alexdevs.solstice.modules.jail.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.jail.JailModule;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

public class UnjailCommand extends ModCommand<JailModule> {
    public UnjailCommand(JailModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("unjail");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return CommandManager.literal(name)
                .requires(require("unjail", 2))
                .then(CommandManager.argument("user", GameProfileArgumentType.gameProfile())
                        .executes(context -> {
                            var profile = getUser(context);

                            var data = module.getPlayer(profile.getId());

                            if (!data.jailed) {
                                context.getSource().sendFeedback(() -> module.locale().get("notJailed"), false);
                                return 0;
                            }

                            module.unjailPlayer(profile.getId());

                            var map = Map.of(
                                    "player", Text.of(profile.getName())
                            );
                            context.getSource().sendFeedback(() -> module.locale().get("unjailed", map), false);

                            return 1;
                        })
                );
    }

    private static GameProfile getUser(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var profiles = GameProfileArgumentType.getProfileArgument(context, "user");
        if (profiles.size() > 1) {
            throw EntityArgumentType.TOO_MANY_PLAYERS_EXCEPTION.create();
        }

        return profiles.iterator().next();
    }
}
