package me.alexdevs.solstice.modules.customname.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class CustomNameConfig {
    @ConfigSerializable
    public record NameFormat(String group, String format) {
    }

    @Comment("Customize player display names based on their LuckPerms group. Priority is determined by the list order: first comes before last.")
    public ArrayList<NameFormat> nameFormats = new ArrayList<>(List.of(
            new NameFormat("admin", "${prefix}<red>${name}</red>${suffix}"),
            new NameFormat("default", "${prefix}<green>${name}</green>${suffix}")
    ));
}
