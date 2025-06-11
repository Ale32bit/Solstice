package me.alexdevs.solstice.modules.utilities.virtualScreenHandlers;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.GrindstoneMenu;

public class VirtualGrindstoneScreenHandler extends GrindstoneMenu {
    public VirtualGrindstoneScreenHandler(int syncId, Inventory playerInventory, ContainerLevelAccess context) {
        super(syncId, playerInventory, context);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
