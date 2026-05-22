# Sudo

This module adds commands to run commands as server or as someone else on the server.

Command output is redirected to the one that executed the sudo/doas command.

## Commands

!!! command "sudo &lt;command&gt;"

    Run a command as server.

    The command is ran with maximum privileges (operator level 4).

    **Permissions**

    * `solstice.sudo.sudo` - Default: 4

!!! command "doas &lt;player&gt; &lt;command&gt;"

    Run a command as another player.

    The command is ran with the target player privileges.

    **Permissions**

    * `solstice.sudo.doas` - Default: 4