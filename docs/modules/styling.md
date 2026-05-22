# Styling

This module adds chat formatting and limited toggleable Markdown formatting to player chats.

Also you can replace chunks of text with something else.

Players with the `solstice.chat.advanced` permission node also unlock the full capabilities of the text formatting. See [Text Formatting](../textformatting.md).

## Configuring

These options can be configured in the `styling` section of the config file.

!!! config "enable-markdown"

    Enable Markdown in chat.

    **Format**

    - **bold** `**bold**`
    - *italic* `*italic*`
    - <u>underline</u> `__underline__`
    - ~~strikethrough~~ `~~strikethrough~~`
    - [Links](https://example.com) `[URLs](https://example.com)` (it shows original URL when hovering over it)
    - `quotes` `` `quotes` ``
    - ||spoilers|| `||spoilers||` (it shows text when hovering over it)

    ![Markdown](../assets/features/markdown.webp)

!!! config "welcome-new-players"

    Send a welcome message to players joining the server for the first time.

!!! config "replacements"

    Key-Value-Pair list of words to be replaced in chat.

    You can add a replacement by adding a line like this to the list:

    ```hocon
    "original"="replacement"
    ```

!!! info "Advancement configurations"

    There are 3 kinds of advancements, called frames: **task**, **challenge**, **goal**.

    ![Advancements](../assets/text_formatting/advancements.png)

    You can read more about them in the [Minecraft Wiki](https://minecraft.wiki/w/Advancement).

    The advancement titles and descriptions are automatically translated to the client's locale.

    !!! config "advancement-task"

        Format for task advancements.

        **Placeholders**

        * `${title}` - Title of the advancement.
        * `${description}` - Description of the advancement.

    !!! config "advancement-challenge"

        Format for challenge advancements.

        **Placeholders**

        - `${title}` - Title of the advancement.
        - `${description}` - Description of the advancement.

    !!! config "advancement-goal"

        Format for goal advancements.

        
        **Placeholders**

        - `${title}` - Title of the advancement.
        - `${description}` - Description of the advancement.

!!! config "chat-format"

    Player chat format.

    **Placeholders**

    * `${message}` - Message after formatting.

    ![Chat message](../assets/text_formatting/chat.webp)

!!! config "emote-format"

    Emote format (`/me`).

!!! config "join-format"

    Player join message format.

!!! config "join-renamed-format"

    Player join message after changing username.

    **Placeholders**

    * `${previousName}` - Previous name of the player.

!!! config "leave-format"

    Player leave message format.

!!! config "death-format"

    Player death message format.

    **Placeholders**

    * `${message}` - The death message, it already contains the player name.

    ![Death](../assets/text_formatting/death.png)

!!! config "welcome"

    New player welcome message.