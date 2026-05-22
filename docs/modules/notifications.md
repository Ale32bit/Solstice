# Notifications

Notifications notify the player of the following actions:

- Received a TPA request.
- Received a direct message (`/tell`).
- Received a player message containing your username.

Players can configure the notifications sound, pitch, volume, and other settings.

## Configuration

### default-values

Default values are used when the player did not explicitly configure their own settings.

!!! config "sound-id"

    Default sound to use.

!!! config "pitch"

    Default pitch to use.

!!! config "volume"

    Default volume to use.

    `0.0` is 0%, `1.0` is 100%, `2.0` is 200%.

!!! config "afk-only"

    Whether to only notify if the player is AFK.

!!! config "on-chat"

    Whether to notify mentions in chat.

## Commands

!!! command "notifications set &lt;option&gt; &lt;value&gt;"

    Change a personal setting for notifications:

    **Options**

    - `sound`: Change sound. Any value is valid and sounds implemented by resource packs in clients are supported.
    - `pitch`: Change sound pitch.
    - `volume`: Change sound volume, in percentage. 200% is max.
    - `afk-only`: Whether to get notified only if AFK.
    - `on-chat`: Whether to get notified if someone mentioned you in chat.

    **Permissions**

    * `solstice.notifications.base` - Default: true

!!! command "notifications get"

    Get your current notification settings.

    **Permissions**

    * `solstice.notifications.base` - Default: true
  
!!! command "notifications toggle"

    Toggle notifications.

    **Permissions**

    * `solstice.notifications.base` - Default: true

!!! command "notifications reset"

    Reset your notification settings.

    Default values will be used instead.

    **Permissions**

    * `solstice.notifications.base` - Default: true