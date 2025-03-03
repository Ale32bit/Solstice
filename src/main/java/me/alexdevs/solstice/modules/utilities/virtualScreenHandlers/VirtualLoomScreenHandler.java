package me.alexdevs.solstice.modules.utilities.virtualScreenHandlers;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.LoomMenu;

public class VirtualLoomScreenHandler extends LoomMenu {
    public VirtualLoomScreenHandler(int syncId, Inventory playerInventory, ContainerLevelAccess context) {
        super(syncId, playerInventory, context);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
