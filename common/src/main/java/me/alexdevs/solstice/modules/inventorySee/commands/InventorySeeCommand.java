package me.alexdevs.solstice.modules.inventorySee.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import me.alexdevs.solstice.api.command.LocalGameProfile;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.permissions.Permissions;
import me.alexdevs.solstice.api.utils.PlayerUtils;
import me.alexdevs.solstice.integrations.TrinketsApiProxy;
import me.alexdevs.solstice.integrations.TrinketsIntegration;
import me.alexdevs.solstice.modules.inventorySee.ImmutableSlot;
import me.alexdevs.solstice.modules.inventorySee.InventorySeeModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class InventorySeeCommand extends ModCommand<InventorySeeModule> {
    public InventorySeeCommand(InventorySeeModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("invsee", "inventorysee");
    }

    private static final LinkedHashMap<Integer, MenuType<ChestMenu>> invSizes = new LinkedHashMap<>();

    static {
        invSizes.put(9, MenuType.GENERIC_9x1);
        invSizes.put(18, MenuType.GENERIC_9x2);
        invSizes.put(27, MenuType.GENERIC_9x3);
        invSizes.put(36, MenuType.GENERIC_9x4);
        invSizes.put(45, MenuType.GENERIC_9x5);
        invSizes.put(54, MenuType.GENERIC_9x6);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name).requires(require(2))
                .then(argument("player", StringArgumentType.word()).suggests(LocalGameProfile::suggest)
                        .executes(context -> {
                            var source = context.getSource();
                            var player = source.getPlayerOrException();
                            var targetProfile = LocalGameProfile.getProfile(context, "player");
                            var targetOnline = PlayerUtils.isOnline(targetProfile.getId());

                            if (!targetOnline && !Permissions.check(player, getPermissionNode("offline"), 3)) {
                                source.sendSuccess(() -> module.locale().get("offlineNotAllowed"), false);
                                return 0;
                            }

                            ServerPlayer target;
                            if (targetOnline) {
                                target = context.getSource()
                                        .getServer()
                                        .getPlayerList()
                                        .getPlayer(targetProfile.getId());
                                if (Permissions.check(target, getPermissionNode("exempt"), 3)) {
                                    source.sendSuccess(() -> module.locale().get("exempt"), false);
                                    return 0;
                                }
                            } else {
                                target = PlayerUtils.loadOfflinePlayer(targetProfile);
                                if (Permissions.check(targetProfile, getPermissionNode("exempt"), 3, source.getServer())
                                        .getNow(false)) {
                                    source.sendSuccess(() -> module.locale().get("exempt"), false);
                                    return 0;
                                }
                            }

                            var canEdit = Permissions.check(player, getPermissionNode("edit"), 3);

                            var targetInventory = target.getInventory();

                            var container = new SimpleGui(MenuType.GENERIC_9x5, player, false) {
                                @Override
                                public void onClose() {
                                    if (!targetOnline) {
                                        PlayerUtils.saveOfflinePlayer(target);
                                    }
                                }
                            };

                            for (var i = 0; i < targetInventory.getContainerSize(); i++) {
                                Slot slot;
                                if (canEdit) {
                                    slot = new Slot(targetInventory, i, 0, 0);
                                } else {
                                    slot = new ImmutableSlot(targetInventory, i, 0, 0);
                                }
                                container.setSlotRedirect(i, slot);
                            }

                            var barrier = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
                            barrier.set(DataComponents.CUSTOM_NAME, Component.literal(""));
                            for (var i = targetInventory.getContainerSize(); i < container.getSize(); i++) {
                                container.setSlot(i, barrier);
                            }

                            container.setTitle(target.getName());

                            container.open();

                            var map = Map.of("user", Component.nullToEmpty(target.getGameProfile().getName()));
                            source.sendSuccess(() -> module.locale().get("openedInventory", map), true);

                            return 1;
                        })
                        .then(literal("trinkets").executes(context -> {
                            var source = context.getSource();
                            var player = source.getPlayerOrException();
                            var targetProfile = LocalGameProfile.getProfile(context, "player");
                            var targetOnline = PlayerUtils.isOnline(targetProfile.getId());

                            if (!targetOnline && !Permissions.check(player, getPermissionNode("offline"), 3)) {
                                source.sendSuccess(() -> module.locale().get("offlineNotAllowed"), false);
                                return 0;
                            }

                            ServerPlayer target;
                            if (targetOnline) {
                                target = context.getSource()
                                        .getServer()
                                        .getPlayerList()
                                        .getPlayer(targetProfile.getId());
                                if (Permissions.check(target, getPermissionNode("exempt"), 3)) {
                                    source.sendSuccess(() -> module.locale().get("exempt"), false);
                                    return 0;
                                }
                            } else {
                                target = PlayerUtils.loadOfflinePlayer(targetProfile);
                                if (Permissions.check(targetProfile, getPermissionNode("exempt"), 3, source.getServer())
                                        .getNow(false)) {
                                    source.sendSuccess(() -> module.locale().get("exempt"), false);
                                    return 0;
                                }
                            }

                            if (!TrinketsIntegration.isAvailable()) {
                                source.sendSuccess(() -> module.locale().get("trinketsNotInstalled"), false);
                                return 0;
                            }

                            var canEdit = Permissions.check(player, getPermissionNode("edit"), 3);
                            var trinkets = TrinketsApiProxy.get().getTrinketComponentInventory(target).orElse(null);
                            var slots = new ArrayList<Slot>();

                            for (var group : trinkets.values()) {
                                for (var inventory : group.values()) {
                                    for (var i = 0; i < inventory.getContainerSize(); i++) {
                                        Slot slot;

                                        if (canEdit) {
                                            slot = new Slot(inventory, i, 0, 0);
                                        } else {
                                            slot = new ImmutableSlot(inventory, i, 0, 0);
                                        }

                                        slots.add(slot);
                                    }
                                }
                            }

                            var size = slots.size();
                            MenuType<ChestMenu> handlerType = null;
                            for (var entry : invSizes.entrySet()) {
                                handlerType = entry.getValue();
                                if (size <= entry.getKey()) {
                                    break;
                                }
                            }

                            var container = new SimpleGui(handlerType, player, false) {
                                @Override
                                public void onClose() {
                                    if (!targetOnline) {
                                        PlayerUtils.saveOfflinePlayer(target);
                                    }
                                }
                            };

                            for (var i = 0; i < slots.size(); i++) {
                                var slot = slots.get(i);
                                container.setSlotRedirect(i, slot);
                            }

                            var barrier = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
                            barrier.set(DataComponents.CUSTOM_NAME, Component.literal(""));
                            for (var i = size; i < container.getSize(); i++) {
                                container.setSlot(i, barrier);
                            }

                            container.setTitle(target.getName());
                            container.open();

                            var map = Map.of("user", Component.nullToEmpty(target.getGameProfile().getName()));
                            source.sendSuccess(() -> module.locale().get("openedTrinkets", map), true);

                            return 1;
                        })));
    }
}
