package me.alexdevs.solstice.modules.rtp.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.rtp.RTPModule;
import me.alexdevs.solstice.modules.rtp.core.Locator;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
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
                .executes(context -> execute(context, false))
                .then(argument("biome", RegistryEntryArgumentType.registryEntry(commandRegistry, RegistryKeys.BIOME))
                        .requires(require("biome.base", 2))
                        .suggests((context, builder) -> {
                            if (Permissions.check(context.getSource(), getPermissionNode("exempt.biome"), 2)) {
                                return builder.buildFuture();
                            }

                            var biomes = getAllowedBiomes(context.getSource(), context.getSource().getWorld());
                            return CommandSource.suggestMatching(biomes, builder);
                        })
                        .executes(context -> execute(context, true))
                );
    }

    private int execute(CommandContext<ServerCommandSource> context, boolean withBiome) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        var config = module.getConfig();

        if (config.requireWorldPermission) {
            var worldName = player.getServerWorld().getRegistryKey().getValue().toString();
            if (!Permissions.check(context.getSource(), getPermissionNode("worlds." + worldName), 2)) {
                context.getSource().sendFeedback(() -> module.locale().get("noWorldPermission", Map.of("world", Text.of(worldName))), false);
                return 0;
            }
        }

        RegistryKey<Biome> biome = null;
        if (withBiome) {
            var biomeEntry = RegistryEntryArgumentType.getRegistryEntry(context, "biome", RegistryKeys.BIOME);
            biome = biomeEntry.getKey().orElse(null);

            if (biomeEntry.getKey().isPresent()) {
                if (!Permissions.check(context.getSource(), getPermissionNode("exempt.biome"), 2)) {
                    var biomeId = biome.getValue().toString();
                    var allowedBiomes = getAllowedBiomes(context.getSource(), context.getSource().getWorld());
                    if (!allowedBiomes.contains(biomeId)) {
                        context.getSource().sendFeedback(() -> module.locale().get("noBiomePermission"), false);
                        return 0;
                    }
                }
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

        Locator locator;
        if (!withBiome) {
            locator = module.createLocator(player);
        } else {
            locator = module.createLocatorWithBiome(player, biome);
        }

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
    }

    private List<String> getAllowedBiomes(ServerCommandSource source, ServerWorld world) {
        var groups = getAllowedGroups(source, world);
        var biomes = new ArrayList<String>();
        groups.forEach(group -> biomes.addAll(getBiomesInGroup(world, group)));
        return biomes;
    }

    private List<String> getBiomesInGroup(ServerWorld world, String group) {
        var config = module.getConfig();
        var worlds = config.biomeGroups;
        var worldId = world.getRegistryKey().getValue().toString();
        if (!worlds.containsKey(worldId)) {
            return List.of();
        }

        var groups = worlds.get(worldId);
        if (!groups.containsKey(group)) {
            return List.of();
        }

        return groups.get(group);
    }

    private List<String> getAllowedGroups(ServerCommandSource source, ServerWorld world) {
        var worldId = world.getRegistryKey().getValue().toString();
        if (Permissions.check(source, getPermissionNode("biomes." + worldId + ".base"), 2)) {
            var config = module.getConfig();
            return config.biomeGroups.getOrDefault(worldId, Map.of())
                    .keySet().stream()
                    .filter(name -> Permissions.check(source, getPermissionNode("biomes." + worldId + "." + name), 2))
                    .toList();
        }
        return List.of();
    }
}
