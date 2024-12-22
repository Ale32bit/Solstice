package me.alexdevs.solstice.modules.mute.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.mute.MuteModule;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class UnmuteCommand extends ModCommand<MuteModule> {
    public UnmuteCommand(MuteModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("unmute");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(2))
                .then(argument("targets", GameProfileArgumentType.gameProfile())
                        .executes(context -> {
                            var targets = GameProfileArgumentType.getProfileArgument(context, "targets");

                            var names = targets.stream().map(GameProfile::getName).toArray(String[]::new);

                            targets.forEach(profile -> {
                                var playerData = module.getPlayerData(profile.getId());
                                playerData.muted = false;
                            });

                            Solstice.playerData.saveAll();

                            context.getSource().sendFeedback(() -> Text.literal("Unmuted " + String.join(", ", names)), true);

                            return 1;
                        }));
    }
}
