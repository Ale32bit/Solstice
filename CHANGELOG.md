## Warning
Backup config.conf, locale.json, modules.conf and `world/data/solstice` before upgrading!

## Changelog

- Revamped module ID system. 
- Add more configuration options to `/back`.
  - Persist location between server restarts and disconnections.
  - Automatic clear for online and offline players.
- Made cooldown available to all Solstice commands.
- Added `/cooldown` command for administrators to clear cooldowns.
- Join and leave messages can be silenced to players with `solstice.chat.activity.silent` permission node.
- Added `/solstice debug enable <[true|false]>` to debug command errors.
- Added `/heal` and `/feed` locale.
- Players can now safely teleport via `/back` and `/home` without getting stuck in walls or falling.
- Added option to send a player to global spawn on respawn if the player does not have a spawnpoint (e.g. bed).
- Chat replacements now support placeholders.
- Added legacy chat format via `solstice.chat.legacy` permission node (e.g. `&a` for light green color).
- Fixed `/kits` not displaying kits correctly.
- Fixed command spy not working correctly. (LuckPerms commands will spam logs, looking for a solution). 
- Added locale support to mute module.
- Added a timespan argument to `/mute` for temporary mute.
- Added locale to `/top`.
- Fixed global spawn for new players.
- Made homes and warps alphabetically ordered in lists.
- Fixed `/fly` not disabling flight while flying.
- Added `/speed` command to change the walking and flight speed.
- Added per-group chat formatting.
- Fixed `/tpa` and `/tpahere` crashing when ignore module is disabled.
- Fixed chat formatting crashing when styling module is disabled.
- Improved chat message formatting performance.
- *Probably other bug fixes...*