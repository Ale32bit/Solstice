package me.alexdevs.solstice.core.cooldown;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.List;
import java.util.Map;

@ConfigSerializable
public class CooldownConfig {
    @Comment("Key-value pairs of commands to check for cooldown. You can use Regular Expressions as command patterns.\nCommand = seconds to cool down.")
    public Map<String, CooldownSetting> nodes = Map.ofEntries(
            Map.entry("rtp", CooldownSetting.of(List.of("rtp", "rtp.*"), 600))
    );
}
