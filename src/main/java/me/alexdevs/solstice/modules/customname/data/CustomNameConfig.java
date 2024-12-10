package me.alexdevs.solstice.modules.customname.data;

import me.alexdevs.solstice.config.Config;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class CustomNameConfig {
    @ConfigSerializable
    public record NameFormat(String group, String format) {
    }

    @Comment("Customize player display names based on their LuckPerms group.")
    public ArrayList<Config.Formats.NameFormat> nameFormats = new ArrayList<>(List.of(
            new Config.Formats.NameFormat("admin", "<red>${name}</red>"),
            new Config.Formats.NameFormat("default", "<green>${name}</green>")
    ));
}
