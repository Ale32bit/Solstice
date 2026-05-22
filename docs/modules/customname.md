# Custom Name

The custom name modules brings customizable player display names and nicknames!

With this module you can configure the player name based on the rank.

## Configuration

### Player name formats

It is possible to customize how player names are displayed in the server based on the LuckPerms group of the player.

!!! info

    If LuckPerms is not installed, or it is not possible to guess the group, Solstice falls back to two group names:

    - `default` - Used for everyone.
    - `operator` - Used by operators.

#### Configure player names

Display names are configurable in the `name-formats` list.

This list uses the order as priority of the group, first elements come before later elements.

!!! example

    If the player is in the `vip` and `default` groups, but is not in the `admin` group, it will pick the `vip` group and skip `default` as `vip` comes before `default`.

    - admin
    - vip
    - default

##### Adding a format

To add a format for a group, add the following lines in the list (remember the order priority in the list):

```hocon
{
    format="<blue>${name}</blue>" # Name format here
    group="mygroup" # Group name
}
```

###### Special Placeholders

These placeholders are pre-processed before doing any formatting parse.
This is to allow LuckPerms' prefix and suffix to correctly apply the formatting, including the name.

* `${name}`: Display name of the player. Respects nicknames unlike `%player:name%`.
* `${prefix}`: LuckPerms prefix. Empty by default.
* `${suffix}`: LuckPerms suffix. Empty by default.

## Commands

!!! command "nickname &quot;&lt;nickname&gt;&quot;"

    Set your nickname.

    **Permissions**

    * `solstice.customname.base` - Default: 2<br>

    **Aliases**

    * nick

!!! command "nickname clear"

    Clear your nickname

    **Permissions**

    * `solstice.customname.base` - Default: 2<br>

    **Aliases**

    * nick

!!! command "nickname &lt;player&gt; &quot;&lt;nickname&gt;&quot;"

    Set the nickname of a player.

    **Permissions**

    * `solstice.customname.others` - Default: 2<br>

    **Aliases**

    * nick

!!! command "nickname &lt;player&gt; clear"

    Clear a player nickname.

    **Permissions**

    * `solstice.customname.others` - Default: 2<br>

    **Aliases**

    * nick