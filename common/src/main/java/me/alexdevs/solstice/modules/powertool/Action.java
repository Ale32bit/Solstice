package me.alexdevs.solstice.modules.powertool;

import java.util.Arrays;
import net.minecraft.util.StringRepresentable;

public enum Action implements StringRepresentable {
    USE,
    ATTACK_BLOCK,
    ATTACK_ENTITY,
    INTERACT_BLOCK,
    INTERACT_ENTITY;

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }

    public static String[] stringValues() {
        return Arrays.stream(values()).map(Action::getSerializedName).toArray(String[]::new);
    }
}
