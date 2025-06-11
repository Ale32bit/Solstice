package me.alexdevs.solstice.modules.utilities.virtualScreenHandlers;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;

public class VirtualAnvilScreenHandler extends AnvilMenu {
    public VirtualAnvilScreenHandler(int syncId, Inventory inventory, ContainerLevelAccess context) {
        super(syncId, inventory, context);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
