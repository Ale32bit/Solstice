package me.alexdevs.solstice.api.utils;

import java.util.Optional;

public class MathUtils {
    public static Optional<Double> parseDouble(String value) {
        try {
            return Optional.of(Double.parseDouble(value));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
