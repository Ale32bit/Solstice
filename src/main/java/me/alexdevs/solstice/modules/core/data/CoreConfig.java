package me.alexdevs.solstice.modules.core.data;

import org.spongepowered.configurate.objectmapping.meta.Comment;

public class CoreConfig {
    @Comment("Generic date format to use.\nMetric format: dd/MM/yyyy\nUSA format: MM/dd/yyyy")
    public String dateFormat = "dd/MM/yyyy";

    @Comment("Generic time format to use.\n24h format: HH:mm\n12h format: hh:mm a")
    public String timeFormat = "HH:mm";

    @Comment("Generic date + time format to use.")
    public String dateTimeFormat = "dd/MM/yyyy HH:mm";

    @Comment("Format to use when displaying links in chat.")
    public String link = "<c:#8888ff><u>${label}</u></c>";

    @Comment("Format to use when hovering over the link in chat.")
    public String linkHover = "${url}";
}
