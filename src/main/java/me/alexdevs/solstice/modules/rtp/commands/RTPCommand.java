package me.alexdevs.solstice.modules.rtp.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.utils.RegistryUtils;
import me.alexdevs.solstice.modules.rtp.RTPModule;
import me.alexdevs.solstice.modules.rtp.core.Locator;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class RTPCommand extends ModCommand<RTPModule> {
    public RTPCommand(RTPModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("rtp");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(2))
                .executes(context -> execute(context, false))
                .then(argument("biome", ResourceArgument.resource(commandRegistry, Registries.BIOME))
                        .requires(require("biome.base", 2))
                        .suggests((context, builder) -> {
                            if (Permissions.check(context.getSource(), getPermissionNode("exempt.biome"), 2)) {
                                var biomeRegistry = this.commandRegistry.lookup(Registries.BIOME);
                                var biomes = RegistryUtils.getBiomes(biomeRegistry.get(), false);
                                return SharedSuggestionProvider.suggest(biomes, builder);
                            }

                            var biomes = getAllowedBiomes(context.getSource(), context.getSource().getLevel());
                            return SharedSuggestionProvider.suggest(biomes, builder);
                        })
                        .executes(context -> execute(context, true))
                );
    }

    private int execute(CommandContext<CommandSourceStack> context, boolean withBiome) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        var config = module.getConfig();

        if (config.requireWorldPermission) {
            var worldName = player.serverLevel().dimension().location().toString();
            if (!Permissions.check(context.getSource(), getPermissionNode("worlds." + worldName), 2)) {
                context.getSource().sendSuccess(() -> module.locale().get("noWorldPermission", Map.of("world", Component.nullToEmpty(worldName))), false);
                return 0;
            }
        }

        ResourceKey<Biome> biome = null;
        if (withBiome) {
            var biomeEntry = ResourceArgument.getResource(context, "biome", Registries.BIOME);
            biome = biomeEntry.unwrapKey().orElse(null);

            if (biomeEntry.unwrapKey().isPresent()) {
                if (!Permissions.check(context.getSource(), getPermissionNode("exempt.biome"), 2)) {
                    var biomeId = biome.location().toString();
                    var allowedBiomes = getAllowedBiomes(context.getSource(), context.getSource().getLevel());
                    if (!allowedBiomes.contains(biomeId)) {
                        context.getSource().sendSuccess(() -> module.locale().get("noBiomePermission"), false);
                        return 0;
                    }
                }
            }
        }

        if (config.cooldown.enable) {
            if (!Solstice.cooldown.trigger(player, module.getPermissionNode(), config.cooldown.cooldown)) {
                context.getSource().sendSuccess(() -> Solstice.cooldown.getMessage(player, module.getPermissionNode()), false);
                return 0;
            }
        }

        final var server = context.getSource().getServer();
        final var uuid = player.getUUID();

        Locator locator;
        if (!withBiome) {
            locator = module.createLocator(player);
        } else {
            locator = module.createLocatorWithBiome(player, biome);
        }

        locator.locate(result -> {
            var newPlayer = server.getPlayerList().getPlayer(uuid);
            if (newPlayer == null) {
                Solstice.LOGGER.info("RTP spot found, but player left.");
                return;
            }
            if (result.position().isPresent() && result.type() == Locator.Result.Type.SUCCESS) {
                player.sendSystemMessage(module.locale().get("success"));
                result.position().get().teleport(player);
            } else {
                final var text = switch (result.type()) {
                    case TOO_MANY_ATTEMPTS -> module.locale().get("tooManyAttempts");
                    case TIMEOUT -> module.locale().get("timeout");
                    case UNSAFE -> module.locale().get("unsafe");
                    default -> Component.nullToEmpty(result.type().toString());
                };
                player.sendSystemMessage(text);

                if (config.cooldown.cancelOnFail) {
                    Solstice.cooldown.clear(player, module.getPermissionNode());
                }
            }
        });

        context.getSource().sendSuccess(() -> module.locale().get("searching"), false);

        return 1;
    }

    private List<String> getAllowedBiomes(CommandSourceStack source, ServerLevel world) {
        var groups = getAllowedGroups(source, world);
        var biomes = new ArrayList<String>();
        groups.forEach(group -> biomes.addAll(getBiomesInGroup(world, group)));
        return biomes;
    }

    private List<String> getBiomesInGroup(ServerLevel world, String group) {
        var config = module.getConfig();
        var worlds = config.biomeGroups;
        var worldId = world.dimension().location().toString();
        if (!worlds.containsKey(worldId)) {
            return List.of();
        }

        var groups = worlds.get(worldId);
        if (!groups.containsKey(group)) {
            return List.of();
        }

        return groups.get(group);
    }

    private List<String> getAllowedGroups(CommandSourceStack source, ServerLevel world) {
        var worldId = world.dimension().location().toString();
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
