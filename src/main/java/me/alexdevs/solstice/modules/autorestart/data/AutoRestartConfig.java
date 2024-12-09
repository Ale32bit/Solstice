package me.alexdevs.solstice.modules.autorestart.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class AutoRestartConfig {
    @Comment("Enable auto restart functionality.")
    public boolean enable = true;

    @Comment("Restart the server at exactly the following times. Time is local.")
    public ArrayList<String> restartAt = new ArrayList<>(List.of(
            "06:00",
            "18:00"
    ));

    @Comment("Sound to play when sending the restart notification in chat.")
    public String restartSound = "minecraft:block.note_block.bell";

    @Comment("Pitch of the sound.")
    public float restartSoundPitch = 0.9f;

    @Comment("Milestones of the restart notifications in seconds.")
    public ArrayList<Integer> restartNotifications = new ArrayList<>(List.of(
            600,
            300,
            120,
            60,
            30,
            15,
            10,
            5,
            4,
            3,
            2,
            1
    ));

}
