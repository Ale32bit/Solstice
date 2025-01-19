package me.alexdevs.solstice.modules.jail.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.command.LocalGameProfile;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.jail.JailModule;
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
                .then(CommandManager.argument("user", StringArgumentType.word())
                        .suggests(LocalGameProfile::suggest)
                        .executes(context -> {
                            var profile = LocalGameProfile.getProfile(context, "user");

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
}
