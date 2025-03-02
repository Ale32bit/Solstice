package me.alexdevs.solstice.modules.note.data;

import java.util.Date;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;

public class Note {
    public UUID createdBy;
    public Date creationDate = new Date();
    public String note;

    public Note() {}

    public Note(String note, UUID createdBy) {
        this.note = note;
        this.createdBy = createdBy;
    }

    public Note(String note, ServerPlayer player) {
        this.note = note;
        this.createdBy = player.getUUID();
    }
}
