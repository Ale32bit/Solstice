# Spawn

This module allows you to configure spawn points in the server.

A first spawn point can be set for new players joining the server for the first time.

## Configuration

!!! config "require-world-permission"

    Require that the player has the permission of the world 'solstice.spawn.worlds.<worldName>' to warp to its spawn, even if the player is in the same world.

    Mind that 'solstice.spawn.worlds.base' is required to be able to use the `world` argument.

### global-spawn

This section defines whether `/spawn` and respawning work on a per world or global server basis.
    

!!! config "on-spawn-command"

    Send the player to the global spawn instead of the world spawn when using the /spawn command.

!!! config "on-respawn"

    Send the player to the global spawn instead of their bed or anchor when respawning.

!!! config "on-login"

    Send the player to the global spawn when logging in.

!!! config "target-spawn-world"

    ID of the world to use as global spawn.
    
    Minecraft dimensions: 'minecraft:overworld', 'minecraft:the_nether', 'minecraft:the_end'.

## Commands

!!! command "spawn [world] [players]"

    Warp to the world spawn point.
    
    If `global-spawn` > `on-spawn-command` is enabled, it will teleport to the configured world's spawn point.

    If the argument `world` is defined it will teleport to the `world`'s spawn point.

    If the argument `players` is defined it will teleport all targets to the `world`'s spawn point.

    **Permissions**

    * `solstice.spawn.base` - Default: true
    * `solstice.spawn.worlds.<worldName>` - Default: 2<br>See `require-world-permission` setting.
    * `solstice.spawn.others` - Default: 2<br>Allow teleporting other players to spawn.

!!! command "setspawn"

    Set the spawn.

    This command also sets the vanilla world spawnpoint.

    **Permissions**

    * `solstice.spawn.set` - Default: 3

!!! command "firstspawn"

    Warp to the first spawn point if available.

    **Permissions**

    * `solstice.spawn.firstspawn` - Default: true

!!! command "setfirstspawn [delete]"

    Set or delete the first spawn point.

    **Permissions**

    * `solstice.spawn.firstspawn.set` - Default: 2