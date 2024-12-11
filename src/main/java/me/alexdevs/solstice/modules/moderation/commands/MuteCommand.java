package me.alexdevs.solstice.modules.moderation.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.locale.Locale;
import me.alexdevs.solstice.modules.moderation.ModerationModule;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MuteCommand extends ModCommand {
    public MuteCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("mute");
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
                                var playerData = ModerationModule.getPlayerData(profile.getId());
                                playerData.muted = true;
                            });

                            Solstice.playerData.saveAll();

                            context.getSource().sendFeedback(() -> Text.literal("Muted " + String.join(", ", names)), true);

                            return 1;
                        }));
    }
}
