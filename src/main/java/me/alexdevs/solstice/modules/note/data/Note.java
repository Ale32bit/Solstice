package me.alexdevs.solstice.modules.note.data;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Date;
import java.util.UUID;

public class Note {
    public UUID createdBy;
    public Date creationDate = new Date();
    public String note;

    public Note() {}

    public Note(String note, UUID createdBy) {
        this.note = note;
        this.createdBy = createdBy;
    }

    public Note(String note, ServerPlayerEntity player) {
        this.note = note;
        this.createdBy = player.getUuid();
    }
}
