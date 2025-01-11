package me.alexdevs.solstice.modules.powertool;

import net.minecraft.util.StringIdentifiable;

import java.util.Arrays;

public enum Action implements StringIdentifiable {
    USE,
    ATTACK_BLOCK,
    ATTACK_ENTITY,
    INTERACT_BLOCK,
    INTERACT_ENTITY;

    @Override
    public String asString() {
        return this.name().toLowerCase();
    }

    public static String[] stringValues() {
        return Arrays.stream(values()).map(Action::asString).toArray(String[]::new);
    }
}
