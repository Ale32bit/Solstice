package me.alexdevs.solstice.modules.utilities.virtualScreenHandlers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;

public class VirtualGrindstoneScreenHandler extends GrindstoneScreenHandler {
    public VirtualGrindstoneScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(syncId, playerInventory, context);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
