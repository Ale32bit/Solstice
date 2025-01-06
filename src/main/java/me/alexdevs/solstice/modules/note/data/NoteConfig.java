package me.alexdevs.solstice.modules.note.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class NoteConfig {
    @Comment("Show a player's note when they login to users with the permission 'solstice.note.showonlogin'")
    public boolean showLogin = true;
}
