package me.alexdevs.solstice.modules.mute.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.mute.MuteModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class UnmuteCommand extends ModCommand<MuteModule> {
    public UnmuteCommand(MuteModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("unmute");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(2))
                .then(argument("targets", GameProfileArgument.gameProfile())
                        .executes(context -> {
                            var targets = GameProfileArgument.getGameProfiles(context, "targets");

                            targets.forEach(profile -> {
                                var playerData = module.getPlayerData(profile.getId());
                                playerData.muted = false;
                                playerData.mutedUntil = null;
                            });

                            Solstice.playerData.saveAll();

                            String localeKey;
                            if (targets.size() == 1) {
                                localeKey = "unmuted";
                            } else {
                                localeKey = "unmutedMultiple";
                            }

                            var placeholders = Map.of(
                                    "count", Component.nullToEmpty(String.valueOf(targets.size())),
                                    "player", Component.nullToEmpty(targets.stream().findFirst().get().getName())
                            );

                            context.getSource().sendSuccess(() -> module.locale().get(localeKey, placeholders), true);

                            return 1;
                        }));
    }
}
