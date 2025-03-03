package me.alexdevs.solstice.modules.utilities.virtualScreenHandlers;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;

public class VirtualCartographyTableScreenHandler extends CartographyTableMenu {
    public VirtualCartographyTableScreenHandler(int syncId, Inventory inventory, ContainerLevelAccess context) {
        super(syncId, inventory, context);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
