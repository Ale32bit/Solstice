# Broadcast

The broadcast module allows you to send a formattable message to all players online.

## Configuration

The broadcast module configuration is under the `broadcast` section of the config file.

!!! config "format"

    Set the format for `/broadcast` messages.

## Commands

!!! command "broadcast &lt;message&gt;"

    Broadcast a message.

    **Permissions**

    * `solstice.broadcast.base` - Default: 2

    **Aliases**

    * bc

!!! command "plainbroadcast &lt;message&gt;"

    Broadcast a formattable message as-is, without using the `format` configuration.

    **Permissions**

    * `solstice.broadcast.plain` - Default: 2

    **Aliases**

    * pbc
