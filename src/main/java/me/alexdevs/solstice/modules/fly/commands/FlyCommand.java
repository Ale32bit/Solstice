package me.alexdevs.solstice.modules.fly.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.fly.FlyModule;
import me.alexdevs.solstice.modules.fly.data.FlyPlayerData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class FlyCommand extends ModCommand<FlyModule> {
    public FlyCommand(FlyModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("fly");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(3))
                .executes(context -> execute(context, null))
                .then(argument("player", EntityArgument.player())
                        .requires(require("others", 3))
                        .executes(context -> execute(context, EntityArgument.getPlayer(context, "player")))
                );
    }

    private int execute(CommandContext<CommandSourceStack> context, @Nullable ServerPlayer player) throws CommandSyntaxException {
        var forOther = player != null;
        if (player == null) {
            player = context.getSource().getPlayerOrException();
        }

        var abilities = player.getAbilities();
        abilities.mayfly = !abilities.mayfly;
        player.onUpdateAbilities();

        var data = Solstice.playerData.get(player).getData(FlyPlayerData.class);
        data.flightEnabled = abilities.mayfly;

        Component text;
        var sourceContext = PlaceholderContext.of(context.getSource());
        if (forOther) {
            var placeholders = Map.of(
                    "player", player.getDisplayName()
            );

            if (abilities.mayfly) {
                text = module.locale().get("enabledForOther", sourceContext, placeholders);
            } else {
                text = module.locale().get("disabledForOther", sourceContext, placeholders);
            }
        } else {
            if (abilities.mayfly) {
                text = module.locale().get("enabled", sourceContext);
            } else {
                text = module.locale().get("disabled", sourceContext);
            }
        }

        context.getSource().sendSuccess(() -> text, forOther);

        return 1;
    }
}
