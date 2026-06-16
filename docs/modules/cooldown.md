# Cooldown

This module enforces cooldowns on commands based on their permission nodes.

Cooldowns are tracked per-player. When a player runs a command that matches a configured key, they must wait the configured number of seconds before using it again.

Players with `solstice.cooldown.exempt.<key>` bypass the cooldown for that key (default: 3).

## Configuration

!!! config "nodes"

    A map of cooldown entries. Each entry key is a name for the cooldown group.

    * `nodes` - List of permission nodes that trigger this cooldown. Supports glob patterns ending in `.*`.
    * `cooldown` - Duration in seconds.

    The default configuration applies a 600-second (10 minute) cooldown to the `rtp` command.

    ```hocon
    nodes {
        rtp {
            nodes=["rtp", "rtp.*"]
            cooldown=600
        }
    }
    ```

## Commands

!!! command "cooldown check &lt;player&gt; &lt;key&gt;"

    Check the remaining cooldown for a player on a given key.

    **Permissions**

    * `solstice.cooldown.check` - Default: 1

!!! command "cooldown clear &lt;player&gt; &lt;key&gt;"

    Clear the cooldown for a player on a given key.

    **Permissions**

    * `solstice.cooldown.clear` - Default: 3
