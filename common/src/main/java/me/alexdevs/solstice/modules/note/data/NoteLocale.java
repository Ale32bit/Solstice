package me.alexdevs.solstice.modules.note.data;

import java.util.Map;

public class NoteLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("deleteButton", "<red>Delete</red>"),
            Map.entry("checkButton", "<gold>Check</gold>"),
            Map.entry("hoverDelete", "Click to delete the note"),
            Map.entry("hoverCheck", "Click to check the note"),
            Map.entry("userNotFound", "<red>User <yellow>${user}</yellow> not found!</red>"),
            Map.entry("noteAdded", "<gold>Note added!</gold>"),
            Map.entry("noteDetails", "<gold>From <yellow>${operator}</yellow> on <yellow>${date}</yellow></gold>\n ${message}\n\n ${deleteButton}"),
            Map.entry("noteListHeader", "<gold><yellow>${user}</yellow>'s notes:</gold>"),
            Map.entry("noteListEntry", "<gold><gray>[${date}]</gray> <yellow>${operator}</yellow>:</gold> ${message} ${checkButton}"),
            Map.entry("notFound", "<red>Note not found</red>"),
            Map.entry("noteDeleted", "<gold>Note deleted!</gold>"),
            Map.entry("emptyNotes", "<gold>There are no notes for this user.</gold>"),
            Map.entry("notesCleared", "<gold>All ${user}'s notes cleared!</gold>"),
            Map.entry("loginInfo", "<gold><yellow>${user}</yellow> has <yellow>${notes}</yellow> notes!</gold> ${checkButton}"),
            Map.entry("addedNotification", "<gold><yellow>${operator}</yellow> added a note to <yellow>${user}</yellow>!</gold> ${checkButton}")
    );
}
