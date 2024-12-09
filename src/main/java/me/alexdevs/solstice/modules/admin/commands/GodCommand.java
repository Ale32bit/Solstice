package me.alexdevs.solstice.modules.admin.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class GodCommand extends ModCommand {
    public GodCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }


    @Override
    public List<String> getNames() {
        return List.of("god");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(3))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    context.getSource().sendFeedback(() -> toggleGod(player), true);

                    return 1;
                })
                .then(argument("player", EntityArgumentType.player())
                        .executes(context -> {
                            var player = EntityArgumentType.getPlayer(context, "player");

                            context.getSource().sendFeedback(() -> toggleGod(player), true);

                            return 1;
                        }));
    }

    private Text toggleGod(ServerPlayerEntity player) {
        var abilities = player.getAbilities();

        abilities.invulnerable = !abilities.invulnerable;
        player.sendAbilitiesUpdate();

        return Text.literal(
                        abilities.invulnerable ?
                                "God mode enabled" :
                                "God mode disabled"
                )
                .append(" for ")
                .append(player.getDisplayName())
                .setStyle(Style.EMPTY.withColor(
                        abilities.invulnerable ?
                                Formatting.GREEN :
                                Formatting.RED
                ));
    }
}
