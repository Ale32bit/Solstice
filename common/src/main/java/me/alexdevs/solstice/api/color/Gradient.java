package me.alexdevs.solstice.api.color;

import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.NotNull;

public class Gradient {
    public static TextColor lerp(final float t, final @NotNull RGBColor a, final @NotNull RGBColor b) {
        final float clampedT = Math.min(1.0f, Math.max(0.0f, t));
        final int ar = a.red();
        final int br = b.red();
        final int ag = a.green();
        final int bg = b.green();
        final int ab = a.blue();
        final int bb = b.blue();
        final var color = new RGBColor(
                Math.round(ar + clampedT * (br - ar)),
                Math.round(ag + clampedT * (bg - ag)),
                Math.round(ab + clampedT * (bb - ab))
        );

        return TextColor.fromRgb(color.getInt());
    }

}
