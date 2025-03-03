package me.alexdevs.solstice.api.color;

import net.minecraft.network.chat.TextColor;

public class RGBColor {
    public final int color;

    public RGBColor(TextColor color) {
        this.color = color.getValue();
    }

    public RGBColor(int color) {
        this.color = color;
    }

    public RGBColor(int red, int green, int blue) {
        this.color = (red << 16) | (green << 8) | blue;
    }

    public RGBColor(byte red, byte green, byte blue) {
        this.color = (red << 16) | (green << 8) | blue;
    }

    public int red() {
        return (color >> 16) & 0xFF;
    }

    public int green() {
        return (color >> 8) & 0xFF;
    }

    public int blue() {
        return color & 0xFF;
    }

    public int getInt() {
        return color;
    }
}
