# Inventory See

This module adds the `/invsee` command to allow you to open a player's inventory like a chest and to manipulate it.

## Commands

!!! command "invsee &lt;player&gt;"

    Open a player's inventory.

    **Permissions**

    * `solstice.inventorysee.base` - Default: 2
    * `solstice.inventorysee.exempt` - Default: 3<br>Make you unable to have your inventory opened via the `/invsee` command.
    * `solstice.inventorysee.offline` - Default: 3<br>Allow accessing the inventory while the target player is offline. **Changes are lost** if the player logs in while the offline inventory is open.
    * `solstice.inventorysee.edit` - Default: 3<br>Allow editing the inventory.

    **Aliases**

    * inventorysee

!!! command "invsee &lt;player&gt; trinkets"

    !!! note inline end "Requires the [Trinkets Mod](https://modrinth.com/mod/trinkets)"

    Open a player's trinkets inventory.

    * `solstice.inventorysee.base` - Default: 2
    * `solstice.inventorysee.exempt` - Default: 3<br>Make you unable to have your inventory opened via the `/invsee` command.
    * `solstice.inventorysee.offline` - Default: 3<br>Allow accessing the inventory while the target player is offline. **Changes are lost** if the player logs in while the offline inventory is open.
    * `solstice.inventorysee.edit` - Default: 3<br>Allow editing the inventory.