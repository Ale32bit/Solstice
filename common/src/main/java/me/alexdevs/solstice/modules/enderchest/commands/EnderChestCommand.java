package me.alexdevs.solstice.modules.enderchest.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import me.alexdevs.solstice.api.command.LocalGameProfile;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.permissions.Permissions;
import me.alexdevs.solstice.api.utils.PlayerUtils;
import me.alexdevs.solstice.modules.enderchest.EnderChestModule;
import me.alexdevs.solstice.modules.inventorySee.ImmutableSlot;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.inventory.Slot;
import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class EnderChestCommand extends ModCommand<EnderChestModule> {
    public EnderChestCommand(EnderChestModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("enderchest", "ec");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrException();
                    player.awardStat(Stats.OPEN_ENDERCHEST);

                    open(player, player.getEnderChestInventory(), Component.translatable("container.enderchest"), true, () -> {
                    });

                    return 1;
                })
                .then(argument("player", StringArgumentType.word())
                        .requires(require("others", 2))
                        .suggests(LocalGameProfile::suggest)
                        .executes(context -> {
                            final var source = context.getSource();
                            var player = source.getPlayerOrException();
                            var profile = LocalGameProfile.getProfile(context, "player");

                            Permissions.check(profile, getPermissionNode("exempt"), 3, source.getServer()).thenAccept(exempt -> {
                                if (exempt) {
                                    source.sendSuccess(() -> module.locale().get("exempt"), false);
                                    return;
                                }

                                var isOnline = PlayerUtils.isOnline(profile.getId());
                                if (!isOnline && !Permissions.check(player, getPermissionNode("offline"), 3)) {
                                    source.sendSuccess(() -> module.locale().get("offlineNotAllowed"), false);
                                    return;
                                }

                                ServerPlayer targetPlayer;

                                if (isOnline) {
                                    targetPlayer = source.getServer().getPlayerList().getPlayer(profile.getId());
                                } else {
                                    targetPlayer = PlayerUtils.loadOfflinePlayer(profile);
                                }

                                var inventory = targetPlayer.getEnderChestInventory();

                                var canEdit = Permissions.check(player, getPermissionNode("edit"), 3);

                                var map = Map.of(
                                        "player", Component.nullToEmpty(profile.getName())
                                );
                                var title = module.locale().get("title", map);

                                open(player, inventory, title, canEdit, () -> {
                                    if(!isOnline) {
                                        PlayerUtils.saveOfflinePlayer(targetPlayer);
                                    }
                                });

                                source.sendSuccess(() -> module.locale().get("opened", map), true);
                            });

                            return 1;
                        }));
    }

    private void open(ServerPlayer player, PlayerEnderChestContainer inventory, Component title, boolean canEdit, Runnable onClose) {
        var container = new SimpleGui(MenuType.GENERIC_9x3, player, false) {
            @Override
            public void onClose() {
                onClose.run();
            }
        };

        for (var i = 0; i < inventory.getContainerSize(); i++) {
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
