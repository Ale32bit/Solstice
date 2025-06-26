package me.alexdevs.solstice.modules.note.data;

import java.util.Date;
import java.util.UUID;

public class Note {
    public UUID createdBy;
    public Date creationDate = new Date();
    public String note;

    public Note(String note, UUID createdBy) {
        this.note = note;
        this.createdBy = createdBy;
    }
}
