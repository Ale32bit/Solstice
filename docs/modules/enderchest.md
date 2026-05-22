# EnderChest

The EnderChest module adds the `/enderchest` command to open your EnderChest (or a player's) GUI without needing the EnderChest block.

## Commands

!!! command "enderchest"

    Open your EnderChest.

    **Permissions**

    * `solstice.enderchest.base` - Default: 2

!!! command "enderchest &lt;player&gt;"

    Open a player's EnderChest, unless the player has the `solstice.enderchest.exempt` permission node.

    **Permissions**

    * `solstice.enderchest.others` - Default: 2
    * `solstice.enderchest.offline` - Default: 3<br>Allow accessing the Ender Chest inventory while the target player is offline. **Changes are lost** if the player logs in while the offline inventory is open.
    * `solstice.enderchest.edit` - Default: 3<br>Allow editing the Ender Chest inventory.

    **Aliases**

    * ec