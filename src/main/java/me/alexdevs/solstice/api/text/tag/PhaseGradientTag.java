package me.alexdevs.solstice.api.text.tag;

import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.parent.GradientNode;
import eu.pb4.placeholders.api.parsers.TextParserV1;
import eu.pb4.placeholders.impl.textparser.TextParserImpl;
import me.alexdevs.solstice.api.Utils;
import me.alexdevs.solstice.api.color.Gradient;
import me.alexdevs.solstice.api.color.RGBColor;
import net.minecraft.text.TextColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * Implement an improved gradient that can also take in the phase.
 * </p>
 * Most of the code is from <a href="https://github.com/KyoriPowered/adventure">Kyori Adventure</a>.
 *
 * @see <a href="https://github.com/KyoriPowered/adventure/blob/91afed95abf8e5ee9ee51c355629e94b1a2b1997/text-minimessage/src/main/java/net/kyori/adventure/text/minimessage/tag/standard/GradientTag.java">Kyori Adventure GradientTag</a>
 */
public class PhaseGradientTag {

    public static TextParserV1.TextTag createTag() {
        return TextParserV1.TextTag.of("phase_gradient", List.of("pgr", "sgr"), "gradient", true,
                (tag, data, input, handlers, endAt) -> {
                    var rawArgs = data.split(":");
                    var out = TextParserImpl.recursiveParsing(input, handlers, endAt);

                    double phase = 0;
                    final List<TextColor> textColors;

                    var args = Arrays.stream(rawArgs).iterator();

                    if (args.hasNext()) {
                        textColors = new ArrayList<>();
                        while (args.hasNext()) {
                            var arg = args.next();
                            if (!args.hasNext()) {
                                final var possiblePhase = Utils.parseDouble(arg);
                                if (possiblePhase.isPresent()) {
                                    phase = Utils.clamp(possiblePhase.get(), -1d, 1d);
                                    break;
                                }
                            }

                            var parsedColor = TextColor.parse(arg);
                            if (parsedColor == null) {
                                parsedColor = TextColor.fromRgb(0);
                            }
                            textColors.add(parsedColor);
                        }

                        if (textColors.size() == 1) {
                            return out.value(GradientNode.colors(textColors, out.nodes()));
                        }
                    } else {
                        textColors = List.of();
                    }

                    return out.value(PhaseGradientTag.smoother(textColors, phase, out.nodes()));
                });
    }

    public static GradientNode smoother(List<TextColor> colors, double phase, TextNode... nodes) {
        if (colors.isEmpty()) {
            colors.add(TextColor.fromRgb(0xffffff));
            colors.add(TextColor.fromRgb(0x000000));
        }
        if (phase < 0) {
            phase += 1;
            Collections.reverse(colors);
        }

        return new GradientNode(nodes, smoothGradient(colors, phase));
    }

    static GradientNode.GradientProvider smoothGradient(List<TextColor> colors, double phase) {
        final var ph = phase * colors.size();
        return (index, length) -> {
            var multiplier = length == 1 ? 0 : (double) (colors.size() - 1) / (length - 1);
            var pos = ((index * multiplier) + ph);
            var lowUnclamped = (int) Math.floor(pos);

            final int high = (int) Math.ceil(pos) % colors.size();
            final int low = lowUnclamped % colors.size();

            return Gradient.lerp((float) pos - lowUnclamped, new RGBColor(colors.get(low)), new RGBColor(colors.get(high)));
        };
    }
}
