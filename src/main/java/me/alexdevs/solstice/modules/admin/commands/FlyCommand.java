package me.alexdevs.solstice.modules.admin.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
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

public class FlyCommand extends ModCommand {
    public FlyCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("fly");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(3))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    context.getSource().sendFeedback(() -> toggleFlight(player), true);

                    return 1;
                })
                .then(argument("player", EntityArgumentType.player())
                        .executes(context -> {
                            var player = EntityArgumentType.getPlayer(context, "player");

                            context.getSource().sendFeedback(() -> toggleFlight(player), true);

                            return 1;
                        }));
    }

    private Text toggleFlight(ServerPlayerEntity player) {
        var abilities = player.getAbilities();

        abilities.allowFlying = !abilities.allowFlying;
        player.sendAbilitiesUpdate();

        return Text.literal(
                        abilities.allowFlying ?
                                "Flight enabled" :
                                "Flight disabled"
                )
                .append(" for ")
                .append(player.getDisplayName())
                .setStyle(Style.EMPTY.withColor(
                        abilities.allowFlying ?
                                Formatting.GREEN :
                                Formatting.RED
                ));
    }
}
