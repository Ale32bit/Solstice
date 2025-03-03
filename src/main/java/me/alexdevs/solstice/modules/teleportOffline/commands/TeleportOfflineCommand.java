package me.alexdevs.solstice.modules.teleportOffline.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.command.LocalGameProfile;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.core.coreModule.data.CorePlayerData;
import me.alexdevs.solstice.modules.teleportOffline.TeleportOfflineModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class TeleportOfflineCommand extends ModCommand<TeleportOfflineModule> {
    public TeleportOfflineCommand(TeleportOfflineModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("tpoffline", "tpoff");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(2))
                .then(argument("player", StringArgumentType.word())
                        .suggests(LocalGameProfile::suggest)
                        .executes(context -> {
                            var source = context.getSource();
                            var player = source.getPlayerOrException();

                            var gameProfile = LocalGameProfile.getProfile(context, "player");

                            var targetData = Solstice.playerData.get(gameProfile).getData(CorePlayerData.class);
                            if (targetData == null || targetData.logoffPosition == null) {
                                source.sendFailure(Component.nullToEmpty("Could not find location of offline player"));
                                return 0;
                            }

                            source.sendSuccess(() -> Component.translatable("commands.teleport.success.entity.single", player.getDisplayName(), Component.nullToEmpty(gameProfile.getName())), true);

                            targetData.logoffPosition.teleport(player, true);
                            return 1;
                        }));
    }
}
