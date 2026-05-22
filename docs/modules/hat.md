# Hat

The hat module adds the `/hat` command, it makes you wear on your head the block/item you are holding.

## Configuration

!!! config "filter"

    !!! info inline end "Tags"

        Check out the command `/solstice debug tags` to get a list of tags for an item.

    List of item IDs and tags that used to filter the item that the player wants to use as hat.

    Check the `whitelist-filter` configuration to configure the behaviour.

!!! config "whitelist-filter"

    Setting this value to `true` will make the `filter` act as a whitelist instead of a blacklist.

    That is, only items and tags in the list are allowed to be used as hat.

    Disabling this setting inverts the behaviour of `filter`.

## Commands

!!! command "hat"

    Wear the item you are holding.

    **Permissions**

    * `solstice.hat.base` - Default: 2