---
hide:
  - navigation
---

# Placeholders

Placeholders are integral parts of customizations to get dynamic values in texts, such as `Hello, %player:name%!` becoming `Hello, Steve!`.

You can check out the list of placeholders here: [https://placeholders.pb4.eu/user/general/](https://placeholders.pb4.eu/user/general/).

## Implemented by Solstice

Solstice adds some extra placeholders that can be used even outside of it, as long as it is installed.

### [AFK](modules/afk.md)

* `%solstice:afk%` - A `[AFK]` tag if the player is AFK, otherwise an empty text.

### Placeholders

* `%entity:uuid%` - The entity's UUID.
* `%entity:name%` - The entity's name. Works exactly like `%player:name%`, but on all entities instead of just players.
* `%entity:displayname%` - The entity's display name. Works exactly like `%player:displayname%`, but on all entities instead of just players.
