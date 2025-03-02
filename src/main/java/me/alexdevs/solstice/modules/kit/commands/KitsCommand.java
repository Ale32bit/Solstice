package me.alexdevs.solstice.modules.kit.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.gui.SimpleGui;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.kit.KitModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import java.util.LinkedHashMap;
import java.util.List;

public class KitsCommand extends ModCommand<KitModule> {
    public KitsCommand(KitModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("kits");
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
        return Commands.literal(name)
                .requires(require(true))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrException();

                    var playerAvailableKits = module.getPlayerKitNames(player);
                    var kits = module.getKits();

                    var size = playerAvailableKits.size();
                    MenuType<ChestMenu> handlerType = null;
                    for (var entry : invSizes.entrySet()) {
                        handlerType = entry.getValue();
                        if (size <= entry.getKey()) {
                            break;
                        }
                    }

                    var gui = new SimpleGui(handlerType, player, false);

                    for (var i = 0; i < Math.min(size, 54); i++) {
                        var kitName = playerAvailableKits.get(i);
                        var kit = kits.get(kitName);

                        var icon = kit.getIcon();
                        icon.setHoverName(Component.nullToEmpty(kitName));

                        var displayNbt = icon.getOrCreateTagElement("display");
                        var list = new ListTag();
                        list.add(StringTag.valueOf(Component.Serializer.toJson(module.locale().get("claimKit"))));
                        displayNbt.put("Lore", list);

                        gui.setSlot(0, new GuiElement(icon, (syncId, clickType, slotActionType) -> {
                            try {
                                dispatcher.execute("kit claim " + kitName, context.getSource());
                            } catch (CommandSyntaxException e) {
                                context.getSource().sendFailure(Component.nullToEmpty(e.getLocalizedMessage()));
                            }
                        }));
                    }

                    gui.setTitle(module.locale().get("kits"));
                    gui.open();

                    return 1;
                });
    }
}
