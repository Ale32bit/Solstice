package me.alexdevs.solstice.modules.god.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.god.GodModule;
import me.alexdevs.solstice.modules.god.data.GodPlayerData;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class GodCommand extends ModCommand<GodModule> {
    public GodCommand(GodModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("god");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(3))
                .executes(context -> execute(context, null))
                .then(argument("player", EntityArgumentType.player())
                        .requires(require("others", 3))
                        .executes(context -> execute(context, EntityArgumentType.getPlayer(context, "player")))
                );
    }

    private int execute(CommandContext<ServerCommandSource> context, @Nullable ServerPlayerEntity player) throws CommandSyntaxException {
        var forOther = player != null;
        if (player == null) {
            player = context.getSource().getPlayerOrThrow();
        }

        var abilities = player.getAbilities();
        abilities.invulnerable = !abilities.invulnerable;
        player.sendAbilitiesUpdate();

        var data = Solstice.playerData.get(player).getData(GodPlayerData.class);
        data.invulnerabilityEnabled = abilities.invulnerable;

        Text text;
        var sourceContext = PlaceholderContext.of(context.getSource());
        if (forOther) {
            var placeholders = Map.of(
                    "player", player.getDisplayName()
            );

            if (abilities.invulnerable) {
                text = module.locale().get("enabledForOther", sourceContext, placeholders);
            } else {
                text = module.locale().get("disabledForOther", sourceContext, placeholders);
            }
        } else {
            if (abilities.invulnerable) {
                text = module.locale().get("enabled", sourceContext);
            } else {
                text = module.locale().get("disabled", sourceContext);
            }
        }

        context.getSource().sendFeedback(() -> text, forOther);

        return 1;
    }
}
