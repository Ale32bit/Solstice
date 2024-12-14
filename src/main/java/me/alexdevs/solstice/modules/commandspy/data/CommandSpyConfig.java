package me.alexdevs.solstice.modules.commandspy.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class CommandSpyConfig {
    @Comment("Commands to ignore.")
    public ArrayList<String> ignoredCommands = new ArrayList<>(List.of(
            "tell", "w", "msg", "dm", "r", "staffchat", "sc", "helpop", "sos"
    ));
}
