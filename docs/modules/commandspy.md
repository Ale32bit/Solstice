# Command Spy

This module broadcasts commands sent by players (regardless of success) to users with the `solstice.commandspy.base` permission node.

## Configuration

!!! config "ignored-commands"

    List of command names that are **not** broadcast to staff. Private commands such as `/tell` and `/staffchat` are excluded by default.

    Default list: `tell`, `w`, `msg`, `dm`, `r`, `staffchat`, `sc`, `helpop`, `sos`.