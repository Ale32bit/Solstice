# Teleport Request

This module adds teleport commands for players.

Players can ask other players to teleport to or be teleported to.

## Configuration

!!! config "teleport-request-timeout"

    Timeout of the teleport request in seconds, the request is automatically canceled after this time elapsed.

    Defaults to `120` seconds.

## Commands

!!! command "tpa &lt;player&gt;"

    Request to teleport to `player`.

    **Permissions**

    * `solstice.teleportrequest.ask` - Default: true

    **Aliases**

    * tpask

!!! command "tpahere &lt;player&gt;"

    Ask `player` to be teleported to your position.

    **Permissions**

    * `solstice.teleportrequest.here` - Default: true

    **Aliases**

    * tpaskhere

!!! command "tpaccept [player]"

    Accept a teleport request.

    Lack of argument `player` will accept the last received request.

    **Permissions**

    * `solstice.teleportrequest.base` - Default: true

    **Aliases**

    * tpyes

!!! command "tpdeny [player]"

    Deny a teleport request.

    Lack of argument `player` will refuse the last received request.

    **Permissions**

    * `solstice.teleportrequest.base` - Default: true

    **Aliases**

    * tpno
    * tprefuse
