package me.alexdevs.solstice.modules.utilities.virtualScreenHandlers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;

public class VirtualAnvilScreenHandler extends AnvilScreenHandler {
    public VirtualAnvilScreenHandler(int syncId, PlayerInventory inventory, ScreenHandlerContext context) {
        super(syncId, inventory, context);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
