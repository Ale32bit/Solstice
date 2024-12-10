package me.alexdevs.solstice.modules.autoannouncement;

import me.alexdevs.solstice.config.Config;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class AutoAnnouncementConfig {
    @ConfigSerializable
    public record Announcement(String text, @Nullable String permission, @Nullable Boolean result) {
        public Announcement(String text) {
            this(text, null, null);
        }
    }

    @Comment("Enable automatic announcements functionality.")
    public boolean enable = true;

    @Comment("Pick the next announcement randomly, else linearly.")
    public boolean pickRandomly = false;
    // every 5 mins
    @Comment("Send announcement every X seconds. Defaults to 300 seconds.")
    public int delay = 300;

    @Comment("Announcement list. Announcements can have a permission as condition. If result is true, the permission has to be granted, else the permission has to be denied (or unset).")
    public ArrayList<Announcement> announcements = new ArrayList<>(List.of(
            new Announcement("Tip! <gray>Solstice is open-source! Contribute on <url:'https://github.com/Ale32bit/Solstice'><blue>GitHub</blue></url>!</gray>"),
            new Announcement("Fun fact! <gray>This announcement is only visible to players that do not have the 'solstice.example' permission granted!</gray>", "solstice.example", false)
    ));
}
