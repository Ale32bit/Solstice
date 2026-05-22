# Core

This module adds core functionality shared between modules and updates players data.

## Configuration

### Date and time format

Some messages may include a date, a time or both.

You can use this [list of patterns](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns) to customize the formats.

!!! config "date-format"

    - Metric: `dd/MM/yyyy`
    - USA: `MM-dd-yyyy`

!!! config "time-format"

    - 24h: `HH:mm`
    - 12h: `hh:mm a`

!!! config "date-time-format"

    *Combination of the above.*

### Links and URLs

You can customize how links are styled in chat.

These formats apply to both the Markdown link style (`[label](url)`) and URLs (`https://example.com`).

!!! config "link"

    - `${label}` - Label of the link, will be URL if not in Markdown style.

!!! config "link-hover"

    This format is displayed when hovering hover the link.

    - `${url}` - URL of the link.

## Commands

!!! command "solstice"

    Show information about the plugin.

    **Permissions**

    * `solstice.core.base` - Default: true<br>

    **Aliases**

    * sol

!!! command "solstice reload"

    Reload Solstice configuration and locale.

    **Permissions**

    * `solstice.core.reload` - Default: 3<br>

!!! command "solstice debug gen-command-list"

    Generate a list of all Solstice commands with permissions.

    **Permissions**

    * `solstice.core.debug` - Default: 4

!!! command "solstice debug tags"

    Get a tag list of the item you are holding.

    **Permissions**

    * `solstice.core.debug` - Default: 4

!!! command "serverstat"

    !!! info inline end "Also check out [Spark](https://spark.lucko.me/) for better statistics!"

    Get statistics of the server such as:

    * Current TPS
    * Uptime
    * Total memory
    * Allocated memory
    * Free memory

    **Permissions**

    * `solstice.core.serverstat` - Default: 3