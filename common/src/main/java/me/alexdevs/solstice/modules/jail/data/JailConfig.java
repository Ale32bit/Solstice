package me.alexdevs.solstice.modules.jail.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.List;

@ConfigSerializable
public class JailConfig {
    @Comment("List of commands the jailed players can execute.")
    public List<String> allowedCommands = List.of(
            "afk",
            "ignore",
            "msg", "tell", "w", "dm", "r", "reply",
            "mail",
            "info", "motd", "rules"
    );

    @Comment("Mute jailed players. They will not be able to send chat messages.")
    public boolean mute = false;
}
