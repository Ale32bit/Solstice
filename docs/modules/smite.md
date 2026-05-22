# Smite

This module adds the `/smite` command to summon lightning bolts either on targets or on the block you look at.

## Commands

!!! command "smite [target] [times]"

    Cast a lightning bolt.

    If the argument `target` is missing, the lightning bolt is cast on the block you are looking at.

    The argument `times` has a default value of `1`.

    The maximum amount of lightning bolts to cast at a time is `1024`, if the limit is reached the summons are evenly divided between all entities. All entities are guaranteed to have at least 1, even if it goes above the limit.

    This command may overload the server, use with caution.

    **Permissions**

    * `solstice.smite.base` - Default: 2