package me.alexdevs.solstice.modules.rtp.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.command.TimeSpan;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.rtp.RTPModule;
import me.alexdevs.solstice.modules.rtp.core.Locator;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.literal;

public class RTPCommand extends ModCommand<RTPModule> {
    public RTPCommand(RTPModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("rtp");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    var config = module.getConfig();

                    if(config.requireWorldPermission) {
                        var worldName = player.getServerWorld().getRegistryKey().getValue().toString();
                        if (!Permissions.check(context.getSource(), getPermissionNode("worlds." + worldName), 2)) {
                            context.getSource().sendFeedback(() -> module.locale().get("noWorldPermission", Map.of("world", Text.of(worldName))), false);
                            return 0;
                        }
                    }

                    if (config.cooldown.enable) {
                        if (!Solstice.cooldown.trigger(player, module.getPermissionNode(), config.cooldown.cooldown)) {
                            context.getSource().sendFeedback(() -> Solstice.cooldown.getMessage(player, module.getPermissionNode()), false);
                            return 0;
                        }
                    }

                    final var server = context.getSource().getServer();
                    final var uuid = player.getUuid();
                    var locator = module.createLocator(player);
                    locator.locate(result -> {
                        var newPlayer = server.getPlayerManager().getPlayer(uuid);
                        if (newPlayer == null) {
                            Solstice.LOGGER.info("RTP spot found, but player left.");
                            return;
                        }
                        if (result.position().isPresent() && result.type() == Locator.Result.Type.SUCCESS) {
                            player.sendMessage(module.locale().get("success"));
                            result.position().get().teleport(player);
                        } else {
                            final var text = switch (result.type()) {
                                case TOO_MANY_ATTEMPTS -> module.locale().get("tooManyAttempts");
                                case TIMEOUT -> module.locale().get("timeout");
                                case UNSAFE -> module.locale().get("unsafe");
                                default -> Text.of(result.type().toString());
                            };
                            player.sendMessage(text);

                            if (config.cooldown.cancelOnFail) {
                                Solstice.cooldown.clear(player, module.getPermissionNode());
                            }
                        }
                    });

                    context.getSource().sendFeedback(() -> module.locale().get("searching"), false);

                    return 1;
                });
    }
}
