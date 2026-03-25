package me.alexdevs.solstice.api.utils;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;

import java.net.URI;

public class ComponentUtils {

    public static HoverEvent showTextHoverEvent(Component hover) {
        //? < 1.21.11
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover);
        //? >= 1.21.11
        //return new HoverEvent.ShowText(hover);
    }

    public static ClickEvent clickCopyToClipboardEvent(String content) {
        //? < 1.21.11
        return new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, content);
        //? >= 1.21.11
        //return new ClickEvent.CopyToClipboard(content);
    }

    public static ClickEvent openUrlClickEvent(String link) {
        //? < 1.21.11
        return new ClickEvent(ClickEvent.Action.OPEN_URL, link);
        //? >= 1.21.11
        //return new ClickEvent.OpenUrl(URI.create(link));
    }
}
