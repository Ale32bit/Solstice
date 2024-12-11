package me.alexdevs.solstice.modules.styling.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class StylingConfig {
    @Comment("Enable Markdown support in chat.")
    public boolean enableMarkdown = true;

    @Comment("Replace text chunks in chat messages.")
    public HashMap<String, String> replacements = new HashMap<>(Map.of(
            ":shrug:", "¯\\\\_(ツ)_/¯"
    ));
}
