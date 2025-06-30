package me.alexdevs.solstice.modules.mute.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.mute.MuteModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;

import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class MuteCommand extends ModCommand<MuteModule> {
    public MuteCommand(MuteModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("mute");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(2))
                .then(argument("targets", GameProfileArgument.gameProfile())
                        .executes(context -> {
                            var targets = GameProfileArgument.getGameProfiles(context, "targets");

                            var names = targets.stream().map(GameProfile::getName).toArray(String[]::new);

                            targets.forEach(profile -> {
                                var playerData = module.getPlayerData(profile.getId());
                                playerData.muted = true;
                            });

                            Solstice.playerData.saveAll();

                            context.getSource().sendSuccess(() -> Component.literal("Muted " + String.join(", ", names)), true);

                            return 1;
                        }));
    }
}
