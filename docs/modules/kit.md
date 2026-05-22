# Kit

This module adds kits.

## Configuration

!!! config "require-permissions"

    Require `solstice.kit.kits.<kit name>` permission node by default to claim a kit.

## Commands

!!! command "kits"

    Open a GUI where the player can claim the kit.

    It can show up to 54 kits for technical reasons.

    **Permissions**

    * `solstice.kit.base` - Default: true

!!! command "kit"

    Base of the command.

    **Permissions**

    * `solstice.kit.base` - Default: true

!!! command "kit list"

    List available kits for the player.

    Unavailable kits (on cooldown or already claimed) are greyed out.

    **Permissions**

    * `solstice.kit.base` - Default: true

!!! command "kit claim &lt;name&gt;"

    Claim a kit.

    If the setting `require-permissions` is enabled, the player needs the permission node `solstice.kit.kits.<kit name>` to claim it.
    
    **Permissions**

    * `solstice.kit.base` - Default: true

!!! command "kit create &lt;name&gt;"

    Create a kit.

    **Permissions**

    * `solstice.kit.set` - Default: 3

!!! command "kit delete &lt;name&gt;"

    Delete a kit.

    **Permissions**

    * `solstice.kit.set` - Default: 3

!!! command "kit edit &lt;name&gt;"

    Edit a kit content.

    **Permissions**

    * `solstice.kit.set` - Default: 3

!!! command "kit set &lt;name&gt; first-join &lt;enable&gt;"

    Toggle flag to automatically claim this kit when a new player joins for the first time in the server.

    **Permissions**

    * `solstice.kit.set` - Default: 3

!!! command "kit set &lt;name&gt; cooldown &lt;timespan&gt;"

    Set a cooldown for the kit before being able to be claimed a second time.

    Kit cooldowns persist in players.

    **Permissions**

    * `solstice.kit.set` - Default: 3

!!! command "kit set &lt;name&gt; one-time &lt;enable&gt;"

    Toggle flag to make it so players can only claim the kit once.

    `first-join` automatically flags the kit as claimed.

    **Permissions**

    * `solstice.kit.set` - Default: 3

!!! command "kit set &lt;name&gt; icon"

    Set the icon of the kit to the item you are currently holding.

    **Permissions**

    * `solstice.kit.set` - Default: 3