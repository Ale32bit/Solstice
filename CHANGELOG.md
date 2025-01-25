# 1.7.0

## Alpha 4

- Added `/nudge` command to slightly push players.
- Added notifications system for TPA requests and direct messages.
- Bugfix homes limit being bypassable.

## Alpha 3

- Fixed `/activetime player` showing as suggestion to players without the permission node.
- Refactored the teleport requests (TPAs) module.
- Bugfix `/staffchat` crashing.
- Implemented tag `<phase_gradient>` (`<pgr>`, `<sgr>`) as an improved gradient algorithm with optional phase, copied from Kyori's Adventure.

## Alpha 2

- `/sleep` now works in daytime too, but does not skip day.
- Revamped how display names are processed, fixing many bugs.

## Alpha 1

- Made modules toggleable (requires restart).
- Added all biomes list to `/rtp [<biome>]` command if `solstic.rtp.exempt.biome` is granted.
- Fixed permissions for `/effects` command.
- Added `/sleep [<entities>]` command.
- Minor bugfixes