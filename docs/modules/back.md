# Back

This module adds the `/back` command and tracks last positions before teleports and death.

## Configuration

!!! config "persist-location"

    Whether to keep the `/back` location saved across server restarts and player reconnections. Default: `true`.

!!! config "clear-timeout"

    Seconds after which the `/back` location of an **online** player is cleared. Set to `-1` to never clear. Default: `-1`.

!!! config "offline-clear-timeout"

    Seconds after which the `/back` location of an **offline** player is cleared. Set to `-1` to never clear. Default: `-1`.

!!! config "safe-check-range"

    Range (in blocks) used when searching for a safe landing position. The check covers a cuboid of this size around the target coordinates.

## Commands

!!! command "back"

    Go to the previous location after teleporting.

    **Permissions**

    * `solstice.back.base` - Default: true<br>
    Enable back command.