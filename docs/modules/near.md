# Near

This module adds the `/near` command to see players near you in a range.

## Configuration

!!! config "max-range"

    Maximum allowed range of the command.

!!! config "default-range"

    Default range of the command.

## Commands

!!! command "near [range]"

    Get the list of players near you.

    The argument `range` defaults to the configuration `default-range`.

    **Permissions**

    * `solstice.near.base` - Default: 2