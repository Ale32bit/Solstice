package me.alexdevs.solstice.modules.enderchest.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import me.alexdevs.solstice.api.command.LocalGameProfile;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.utils.PlayerUtils;
import me.alexdevs.solstice.modules.enderchest.EnderChestModule;
import me.alexdevs.solstice.modules.inventorySee.ImmutableSlot;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class EnderChestCommand extends ModCommand<EnderChestModule> {
    public EnderChestCommand(EnderChestModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("enderchest", "ec");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    player.incrementStat(Stats.OPEN_ENDERCHEST);

                    open(player, player.getEnderChestInventory(), Text.translatable("container.enderchest"), true, () -> {
                    });

                    return 1;
                })
                .then(argument("player", StringArgumentType.word())
                        .requires(require("others", 2))
                        .suggests(LocalGameProfile::suggest)
                        .executes(context -> {
                            final var source = context.getSource();
                            var player = source.getPlayerOrThrow();
                            var profile = LocalGameProfile.getProfile(context, "player");

                            Permissions.check(profile, getPermissionNode("exempt"), 3, source.getServer()).thenAccept(exempt -> {
                                if (exempt) {
                                    source.sendFeedback(() -> module.locale().get("exempt"), false);
                                    return;
                                }

                                var isOnline = PlayerUtils.isOnline(profile.getId());
                                if (!isOnline && !Permissions.check(player, getPermissionNode("offline"), 3)) {
                                    source.sendFeedback(() -> module.locale().get("offlineNotAllowed"), false);
                                    return;
                                }

                                ServerPlayerEntity targetPlayer;

                                if (isOnline) {
                                    targetPlayer = source.getServer().getPlayerManager().getPlayer(profile.getId());
                                } else {
                                    targetPlayer = PlayerUtils.loadOfflinePlayer(profile);
                                }

                                var inventory = targetPlayer.getEnderChestInventory();

                                var canEdit = Permissions.check(player, getPermissionNode("edit"), 3);

                                var map = Map.of(
                                        "player", Text.of(profile.getName())
                                );
                                var title = module.locale().get("title", map);

                                open(player, inventory, title, canEdit, () -> {
                                    if(!isOnline) {
                                        PlayerUtils.saveOfflinePlayer(targetPlayer);
                                    }
                                });

                                source.sendFeedback(() -> module.locale().get("opened", map), true);
                            });

                            return 1;
                        }));
    }

    private void open(ServerPlayerEntity player, EnderChestInventory inventory, Text title, boolean canEdit, Runnable onClose) {
        var container = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false) {
            @Override
            public void onClose() {
                onClose.run();
            }
        };

        for (var i = 0; i < inventory.size(); i++) {
            Slot slot;
            if (canEdit) {
                slot = new Slot(inventory, i, 0, 0);
            } else {
                slot = new ImmutableSlot(inventory, i, 0, 0);
            }
            container.setSlotRedirect(i, slot);
        }

        container.setTitle(title);

        container.open();
    }
}
