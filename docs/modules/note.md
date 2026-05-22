# Note

This module adds notes that operators can add to players for moderation purposes. The player is not aware of these notes.

## Configuration

!!! config "show-login"

    Show a player's note when they login to users with t he permission `solstice.note.showonlogin`.

## Commands

!!! command "note &lt;user&gt;"

    Show the list of notes of a player.

    **Permissions**

    * `solstice.note.base` - Default: 2

!!! command "note &lt;user&gt; add &lt;message&gt;" 

    Add a note to a player.

    **Permissions**

    * `solstice.note.add` - Default: 2
    * `solstice.note.notify` - Default: 2<br>Notify users that the player got a note.

!!! command "note &lt;user&gt; check &lt;index&gt;"

    Check the details of a note.

    This command is usually handled by buttons.

    **Permissions**

    * `solstice.note.base` - Default: 2

!!! command "note &lt;user&gt; delete &lt;index&gt;"

    Delete a note of a player.

    **Permissions**

    * `solstice.note.delete` - Default: 2

!!! command "note &lt;user&gt; clear"

    Delete all notes of a player.

    **Permissions**

    * `solstice.note.clear` - Default: 2