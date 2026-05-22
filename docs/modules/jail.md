# Jail

This module brings jails as a moderation tool.

To setup, build an enclosed space that, ideally, the player cannot escape.

Jailed players cannot build, break blocks, attack entities, interact with entities, use items, and run commands outside of a predefined list in the configuration.

## Configuration

!!! config "allowed-commands"

    List of commands that the jailed players can execute.

!!! config "mute"

    Whether to mute jailed players. They will not be able to send chat messages.

## Commands

!!! command "jails"

    List available jails.

    **Permissions**

    * `solstice.jail.base` - Default: 2

!!! command "jails set &lt;name&gt;"

    Create a jail at your position.

    **Permissions**

    * `solstice.jail.set` - Default: 3

!!! command "jails delete &lt;name&gt;"

    Delete a jail.

    **Permissions**

    * `solstice.jail.delete` - Default: 3

!!! command "jails tp &lt;name&gt;"

    Teleport to a jail.

    * `solstice.jail.tp` - Default: 2

!!! command "jail &lt;user&gt; &lt;jail&gt; [duration] [reason]"

    Jail an user at `jail`.

    If `duration` is not provided it will be unlimited.

    **Permissions**

    * `solstice.jail.jail` - Default: 2

!!! command "unjail &lt;user&gt;"

    Unjail an user.

    **Permissions**

    * `solstice.jail.unjail` - Default: 2

!!! command "checkjail &lt;user&gt;"

    Check the jail status of an user.

    **Permissions**

    * `solstice.jail.base` - Default: 2