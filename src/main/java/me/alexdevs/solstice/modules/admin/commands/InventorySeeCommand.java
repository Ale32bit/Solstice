package me.alexdevs.solstice.modules.admin.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import me.alexdevs.solstice.api.module.ModCommand;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class InventorySeeCommand extends ModCommand {
    public InventorySeeCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("invsee");
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
                                source.sendError(Text.of("You cannot open this inventory because the player is exempt."));
                                return 0;
                            }

                            var targetInventory = player.getInventory();

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

                            return 1;
                        }));
    }
}
