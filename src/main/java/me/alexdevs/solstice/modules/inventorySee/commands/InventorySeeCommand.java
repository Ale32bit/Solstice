package me.alexdevs.solstice.modules.inventorySee.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.inventorySee.InventorySeeModule;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

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

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(2))
                .then(argument("player", EntityArgumentType.player())
                        .executes(context -> {
                            var source = context.getSource();
                            var player = source.getPlayerOrThrow();
                            var target = EntityArgumentType.getPlayer(context, "player");

                            if (Permissions.check(target, getPermissionNode() + ".exempt", 3)) {
                                source.sendFeedback(() -> module.locale().get("exempt"), false);
                                return 0;
                            }

                            var targetInventory = target.getInventory();

                            var container = new SimpleGui(ScreenHandlerType.GENERIC_9X5, player, false);

                            for (var i = 0; i < targetInventory.size(); i++) {
                                container.setSlotRedirect(i, new Slot(targetInventory, i, 0, 0));
                            }

                            var barrier = new ItemStack(Items.BARRIER);
                            barrier.setCustomName(Text.literal(""));
                            for (var i = targetInventory.size(); i < container.getSize(); i++) {
                                container.setSlot(i, barrier);
                            }

                            container.setTitle(target.getDisplayName());

                            container.open();

                            var map = Map.of(
                                    "user", Text.of(target.getGameProfile().getName())
                            );
                            source.sendFeedback(() -> module.locale().get("openedInventory", map), true);

                            return 1;
                        }));
    }
}
