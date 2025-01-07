package me.alexdevs.solstice.modules.teleportOffline.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.core.coreModule.data.CorePlayerData;
import me.alexdevs.solstice.modules.teleportOffline.TeleportOfflineModule;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TeleportOfflineCommand extends ModCommand<TeleportOfflineModule> {
    public TeleportOfflineCommand(TeleportOfflineModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("tpoffline", "tpoff");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(2))
                .then(argument("player", StringArgumentType.word())
                        .executes(context -> {
                            var source = context.getSource();
                            var player = source.getPlayerOrThrow();
                            var server = context.getSource().getServer();

                            var targetName = StringArgumentType.getString(context, "player");

                            server.getUserCache().findByNameAsync(targetName, gameProfile -> {
                                if (gameProfile.isEmpty()) {
                                    source.sendError(Text.of("Could not find player"));
                                    return;
                                }

                                var targetData = Solstice.playerData.get(gameProfile.get()).getData(CorePlayerData.class);
                                if (targetData == null || targetData.logoffPosition == null) {
                                    source.sendError(Text.of("Could not find location of offline player"));
                                    return;
                                }

                                source.sendFeedback(() -> Text.translatable("commands.teleport.success.entity.single", player.getDisplayName(), Text.of(gameProfile.get().getName())), true);

                                targetData.logoffPosition.teleport(player, true);
                            });
                            return 1;
                        }));
    }
}
