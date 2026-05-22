# Player Tab List

The header and footer of the player tab list can be customized with multiple lines of formattable text.

![Customized player tab list](../assets/features/tablist.webp)

## Configuring

The player tab list can be configured from the config file in the `tablist` section.

!!! config "enable"

    Enable customizable player tab list.

!!! config "delay"

    How many milliseconds to wait before updating the tab list.

    Defaults to `250` milliseconds.

!!! config "phase-period"

    It's an oscillating number (sin function).

    How fast the phase is. Lower = faster.

    Defaults to `300`.

    !!! warning "WIP"

        This configuration is used for future purposes.

!!! config "header"

    The header lines of the tab list.
    Each line represents a line of the header and the player placeholder context is applied.

!!! config "footer"

    The footer lines of the tab list.
    Each line represents a line of the footer and the player placeholder context is applied.

!!! config "player-tab-name"

    Format to use when displaying the player name in the list.