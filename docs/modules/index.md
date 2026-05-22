---
title: Introduction
---

# Modules

Modules bring functionalities and commands to Solstice.

Check out the left side bar for a list of documented modules.

## Enable & Disable

!!! warning inline end "Changing these setting requires restarting the server!"

Most, if not all, modules can be disabled by updating the file `config/solstice/modules.conf`, setting the value to `true` will enable the module (default), otherwise use `false`.

Disabling modules help choosing what features of Solstice to use and resolves compatibility issues with other mods that implement the same functionality.

Some modules depend on other modules and will be disabled as well.

## Default permission values

All permissions hold a default value when the permission check returns a non-existant value (is not set).

If the default value is `true`, the permission is granted to everyone, if it's `false` then the permission is denied by default to everyone.

If the value is a number between 0 and 4 (included), it references the operator [permission level](https://minecraft.wiki/w/Permission_level), useful when **LuckPerms** is not installed.

## Command arguments

Many commands hold arguments in the format of &lt;arg&gt; and [arg].

* `<arg>` - The argument is required.
* `[arg]` - The argument is optional.