package me.alexdevs.solstice.modules.hat.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.List;

@ConfigSerializable
public class HatConfig {

    @Comment("Make the filter setting act as a whitelist instead of a blacklist.")
    public boolean whitelistFilter = false;

    @Comment("Items & tags to allow/deny. See the 'whitelist-filter' setting to change the behaviour of this list.\nUse '#' as prefix to filter as tag.")
    public List<String> filter = List.of(
            "#c:shulker_boxes"
    );
}
