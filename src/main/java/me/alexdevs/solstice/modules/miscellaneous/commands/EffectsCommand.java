package me.alexdevs.solstice.modules.miscellaneous.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.alexdevs.solstice.api.command.TimeSpan;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.miscellaneous.MiscellaneousModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Map;

public class EffectsCommand extends ModCommand<MiscellaneousModule> {
    public EffectsCommand(MiscellaneousModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("effects");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return Commands.literal(name)
                .requires(require("effects.base", 1))
                .executes(context -> execute(context, context.getSource().getPlayerOrException()))
                .then(Commands.argument("player", EntityArgument.player())
                        .requires(require("effects.others", 2))
                        .executes(context -> execute(context, EntityArgument.getPlayer(context, "player")))
                );
    }

    private int execute(CommandContext<CommandSourceStack> context, ServerPlayer target) {
        var effects = target.getActiveEffectsMap();
        if (effects.isEmpty()) {
            context.getSource().sendSuccess(() -> module.locale().get("noEffects"), false);
            return 0;
        }

        var text = Component.empty();
        text.append(module.locale().get("effectHeader"));

        for (var entry : effects.entrySet()) {
            text.append("\n");

            var effect = entry.getKey();
            var instance = entry.getValue();

            String duration;
            if (instance.isInfiniteDuration()) {
                duration = module.locale().raw("infinite");
            } else {
                duration = TimeSpan.toShortString(instance.getDuration() / 20);
            }

            var map = Map.of(
                    //? >= 1.21.1
                    "effect", Component.translatable(effect.value().getDescriptionId()),
                    "amplifier", Component.nullToEmpty(String.valueOf(instance.getAmplifier())),
                    "duration", Component.nullToEmpty(duration)
            );
            text.append(module.locale().get("effect", map));
        }

        context.getSource().sendSuccess(() -> text, false);

        return effects.size();
    }

}
