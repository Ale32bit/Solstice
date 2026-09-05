package me.alexdevs.solstice.modules.pagination;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;

import java.io.IOException;
import java.util.HashMap;

public class PaginationModule extends ModuleBase {
    private final HashMap<Character, Integer> fontWidths = new HashMap<>();

    public PaginationModule(SolsticeIdentifier id) {
        super(id);
    }

    @Override
    public void init() {
        SolsticeEvents.READY.register((instance, server) -> {
            var resourceManager = server.getResourceManager();
            var fontResource = resourceManager.getResource(Solstice.ID.withPath("font_widths.json").get()).orElseThrow();
            try (var bufferedReader = fontResource.openAsReader()) {
                var gson = new Gson();
                var type = new TypeToken<HashMap<String, Integer>>(){}.getType();
                HashMap<String, Integer> map = gson.fromJson(bufferedReader, type);
                fontWidths.clear();

                map.forEach((k, v) -> fontWidths.put(k.charAt(0), v));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
