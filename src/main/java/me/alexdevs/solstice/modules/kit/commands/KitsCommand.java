package me.alexdevs.solstice.modules.kit.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.gui.SimpleGui;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.kit.KitModule;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KitsCommand extends ModCommand<KitModule> {
    public KitsCommand(KitModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("kits");
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
        return CommandManager.literal(name)
                .requires(require(true))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();

                    var playerAvailableKits = module.getPlayerKitNames(player);
                    var kits = module.getKits();

                    var size = playerAvailableKits.size();
                    ScreenHandlerType<GenericContainerScreenHandler> handlerType = null;
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
                        icon.setCustomName(Text.of(kitName));

                        var displayNbt = icon.getOrCreateSubNbt("display");
                        var list = new NbtList();
                        list.add(NbtString.of(Text.Serializer.toJson(module.locale().get("claimKit"))));
                        displayNbt.put("Lore", list);

                        gui.setSlot(0, new GuiElement(icon, (syncId, clickType, slotActionType) -> {
                            try {
                                dispatcher.execute("kit claim " + kitName, context.getSource());
                            } catch (CommandSyntaxException e) {
                                context.getSource().sendError(Text.of(e.getLocalizedMessage()));
                            }
                        }));
                    }

                    gui.setTitle(module.locale().get("kits"));
                    gui.open();

                    return 1;
                });
    }
}
