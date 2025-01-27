package me.alexdevs.solstice.modules.inventorySee.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.emi.trinkets.api.TrinketsApi;
import eu.pb4.sgui.api.gui.SimpleGui;
import me.alexdevs.solstice.api.command.LocalGameProfile;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.utils.PlayerUtils;
import me.alexdevs.solstice.integrations.TrinketsIntegration;
import me.alexdevs.solstice.modules.inventorySee.ImmutableSlot;
import me.alexdevs.solstice.modules.inventorySee.InventorySeeModule;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class InventorySeeCommand extends ModCommand<InventorySeeModule> {
    public InventorySeeCommand(InventorySeeModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("invsee", "inventorysee");
    }

    private static final LinkedHashMap<Integer, ScreenHandlerType<GenericContainerScreenHandler>> invSizes = new LinkedHashMap<>();

    static {
        invSizes.put(9, ScreenHandlerType.GENERIC_9X1);
        invSizes.put(18, ScreenHandlerType.GENERIC_9X2);
        invSizes.put(27, ScreenHandlerType.GENERIC_9X3);
        invSizes.put(36, ScreenHandlerType.GENERIC_9X4);
        invSizes.put(45, ScreenHandlerType.GENERIC_9X5);
        invSizes.put(54, ScreenHandlerType.GENERIC_9X6);
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(2))
                .then(argument("player", StringArgumentType.word())
                        .suggests(LocalGameProfile::suggest)
                        .executes(context -> {
                            var source = context.getSource();
                            var player = source.getPlayerOrThrow();
                            var targetProfile = LocalGameProfile.getProfile(context, "player");
                            var targetOnline = PlayerUtils.isOnline(targetProfile.getId());

                            if (!targetOnline && !Permissions.check(player, getPermissionNode("offline"), 3)) {
                                source.sendFeedback(() -> module.locale().get("offlineNotAllowed"), false);
                                return 0;
                            }

                            ServerPlayerEntity target;
                            if (targetOnline) {
                                target = context.getSource().getServer().getPlayerManager().getPlayer(targetProfile.getId());
                                if (Permissions.check(target, getPermissionNode("exempt"), 3)) {
                                    source.sendFeedback(() -> module.locale().get("exempt"), false);
                                    return 0;
                                }
                            } else {
                                target = PlayerUtils.loadOfflinePlayer(targetProfile);
                                if (Permissions.check(targetProfile, getPermissionNode("exempt"), 3, source.getServer()).getNow(false)) {
                                    source.sendFeedback(() -> module.locale().get("exempt"), false);
                                    return 0;
                                }
                            }

                            var canEdit = Permissions.check(player, getPermissionNode("edit"), 3);

                            var targetInventory = target.getInventory();

                            var container = new SimpleGui(ScreenHandlerType.GENERIC_9X5, player, false) {
                                @Override
                                public void onClose() {
                                    if (!targetOnline) {
                                        PlayerUtils.saveOfflinePlayer(target);
                                    }
                                }
                            };

                            for (var i = 0; i < targetInventory.size(); i++) {
                                Slot slot;
                                if (canEdit) {
                                    slot = new Slot(targetInventory, i, 0, 0);
                                } else {
                                    slot = new ImmutableSlot(targetInventory, i, 0, 0);
                                }
                                container.setSlotRedirect(i, slot);
                            }

                            var barrier = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
                            barrier.set(DataComponentTypes.CUSTOM_NAME, Text.literal(""));
                            for (var i = targetInventory.size(); i < container.getSize(); i++) {
                                container.setSlot(i, barrier);
                            }

                            container.setTitle(target.getName());

                            container.open();

                            var map = Map.of(
                                    "user", Text.of(target.getGameProfile().getName())
                            );
                            source.sendFeedback(() -> module.locale().get("openedInventory", map), true);

                            return 1;
                        })
                        .then(literal("trinkets")
                                .executes(context -> {
                                    var source = context.getSource();
                                    var player = source.getPlayerOrThrow();
                                    var targetProfile = LocalGameProfile.getProfile(context, "player");
                                    var targetOnline = PlayerUtils.isOnline(targetProfile.getId());

                                    if (!targetOnline && !Permissions.check(player, getPermissionNode("offline"), 3)) {
                                        source.sendFeedback(() -> module.locale().get("offlineNotAllowed"), false);
                                        return 0;
                                    }

                                    ServerPlayerEntity target;
                                    if (targetOnline) {
                                        target = context.getSource().getServer().getPlayerManager().getPlayer(targetProfile.getId());
                                        if (Permissions.check(target, getPermissionNode("exempt"), 3)) {
                                            source.sendFeedback(() -> module.locale().get("exempt"), false);
                                            return 0;
                                        }
                                    } else {
                                        target = PlayerUtils.loadOfflinePlayer(targetProfile);
                                        if (Permissions.check(targetProfile, getPermissionNode("exempt"), 3, source.getServer()).getNow(false)) {
                                            source.sendFeedback(() -> module.locale().get("exempt"), false);
                                            return 0;
                                        }
                                    }

                                    if (!TrinketsIntegration.isAvailable()) {
                                        source.sendFeedback(() -> module.locale().get("trinketsNotInstalled"), false);
                                        return 0;
                                    }

                                    var canEdit = Permissions.check(player, getPermissionNode("edit"), 3);

                                    var trinkets = TrinketsApi.getTrinketComponent(target).orElse(null);
                                    var slots = new ArrayList<Slot>();
                                    for (var group : trinkets.getInventory().values()) {
                                        for (var inventory : group.values()) {
                                            for (var i = 0; i < inventory.size(); i++) {
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
                                    ScreenHandlerType<GenericContainerScreenHandler> handlerType = null;
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
                                    barrier.set(DataComponentTypes.CUSTOM_NAME, Text.literal(""));
                                    for (var i = size; i < container.getSize(); i++) {
                                        container.setSlot(i, barrier);
                                    }

                                    container.setTitle(target.getName());
                                    container.open();

                                    var map = Map.of(
                                            "user", Text.of(target.getGameProfile().getName())
                                    );
                                    source.sendFeedback(() -> module.locale().get("openedTrinkets", map), true);

                                    return 1;
                                }))
                );
    }
}
