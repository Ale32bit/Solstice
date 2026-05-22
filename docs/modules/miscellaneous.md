# Miscellaneous

This module adds features that feel like they should not be in their own module.

## Commands

!!! command "effects [player]"

    List the active potion effects of a player.

    **Permissions**

    * `solstice.miscellaneous.effects.base` - Default: 1
    * `solstice.miscellaneous.effects.others` - Default: 2

!!! command "sleep [players]"

    Force sleeping without requiring a bed or to be night; does not skip day if already day.

    **Permissions**

    * `solstice.miscellaneous.sleep.base` - Default: 1
    * `solstice.miscellaneous.sleep.others` - Default: 2

!!! command "nudge &lt;entities&gt; [power] [quiet]"

    "Slightly" (depends on the power argument) push the entity in a random direction.

    Use the argument `power` to multiply the push force.

    Use the argument `quiet` to not make a sound when pushing a player.

    **Permissions**

    * `solstice.miscellaneous.nudge.base` - Default: 2

!!! command "top"

    Get to the highest block at your current coordinates.

    **Permissions**

    * `solstice.miscellaneous.top.base` - Default: 2