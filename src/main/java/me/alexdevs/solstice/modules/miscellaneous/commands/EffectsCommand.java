package me.alexdevs.solstice.modules.miscellaneous.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.alexdevs.solstice.api.command.TimeSpan;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.miscellaneous.MiscellaneousModule;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

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
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return CommandManager.literal(name)
                .requires(require(1))
                .executes(context -> execute(context, context.getSource().getPlayerOrThrow()))
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .requires(require("others", 2))
                        .executes(context -> execute(context, EntityArgumentType.getPlayer(context, "player")))
                );
    }

    private int execute(CommandContext<ServerCommandSource> context, ServerPlayerEntity target) {
        var effects = target.getActiveStatusEffects();
        if (effects.isEmpty()) {
            context.getSource().sendFeedback(() -> module.locale().get("noEffects"), false);
            return 0;
        }

        var text = Text.empty();
        text.append(module.locale().get("effectHeader"));

        for (var entry : effects.entrySet()) {
            text.append("\n");

            var effect = entry.getKey();
            var instance = entry.getValue();

            String duration;
            if (instance.isInfinite()) {
                duration = module.locale().raw("infinite");
            } else {
                duration = TimeSpan.toShortString(instance.getDuration() / 20);
            }

            var map = Map.of(
                    "effect", Text.translatable(effect.value().getTranslationKey()),
                    "amplifier", Text.of(String.valueOf(instance.getAmplifier())),
                    "duration", Text.of(duration)
            );
            text.append(module.locale().get("effect", map));
        }

        context.getSource().sendFeedback(() -> text, false);

        return effects.size();
    }

}
