package me.alexdevs.solstice.modules.tablist.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class TabListConfig {
    @Comment("Enable the custom tab list functionality.")
    public boolean enable = true;

    @Comment("Send tab list updates every X milliseconds. Defaults to 250 ms.")
    public int delay = 250;

    @Comment("How fast the phase is. Lower is faster. Defaults to 300")
    public double phasePeriod = 300;

    @Comment("Header lines")
    public ArrayList<String> header = new ArrayList<>(List.of(
            "<gradient:#DEDE6C:#CC4C4C><st>                                  </st></gradient>"
    ));

    @Comment("Footer lines")
    public ArrayList<String> footer = new ArrayList<>(List.of(
            "<gradient:#CC4C4C:#DEDE6C><st>                                  </st></gradient>"
    ));

    @Comment("Format to use when displaying the player name in the tab list.")
    public String playerTabName = "%solstice:afk%%player:displayname_visual%";
}
