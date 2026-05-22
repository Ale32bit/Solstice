# Ban

The ban module implements formattable ban messages and temporary bans.

## Commands

!!! command "ban &lt;targets&gt; [reason]"

    Ban targets with a formattable reason message.

    **Permissions**

    * `solstice.ban.base` - Default: 3

!!! command "tempban &lt;targets&gt; &lt;timespan&gt; [reason]"

    Temporarily ban targets with a formattable reason message.

    **Permissions**

    * `solstice.ban.tempban` - Default: 3

!!! command "unban &lt;targets&gt;"

    Pardon targets.

    **Permissions**

    * `solstice.ban.base` - Default: 3  