package me.alexdevs.solstice.modules.utilities.virtualScreenHandlers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.CartographyTableScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;

public class VirtualCartographyTableScreenHandler extends CartographyTableScreenHandler {
    public VirtualCartographyTableScreenHandler(int syncId, PlayerInventory inventory, ScreenHandlerContext context) {
        super(syncId, inventory, context);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
