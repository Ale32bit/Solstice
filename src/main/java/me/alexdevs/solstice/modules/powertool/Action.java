package me.alexdevs.solstice.modules.powertool;

import net.minecraft.util.StringRepresentable;

import java.util.Arrays;

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
