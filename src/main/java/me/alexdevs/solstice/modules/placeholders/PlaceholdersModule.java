package me.alexdevs.solstice.modules.placeholders;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleProperties;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;

public class PlaceholdersModule extends ModuleBase {

    public static final String ENTITY = "entity";

    public PlaceholdersModule(ModuleProperties properties) {
        super(properties);
    }

    @Override
    public void init() {
        Placeholders.register(SolsticeIdentifier.of(ENTITY, "name").get(), (context, str) -> {
            if (!context.hasEntity()) {
                return PlaceholderResult.invalid("No entity!");
            }
            var entity = context.entity();
            return PlaceholderResult.value(entity.getName());
        });

        Placeholders.register(SolsticeIdentifier.of(ENTITY, "displayname").get(), (context, str) -> {
            if (!context.hasEntity()) {
                return PlaceholderResult.invalid("No entity!");
            }
            var entity = context.entity();
            return PlaceholderResult.value(entity.getDisplayName());
        });

        Placeholders.register(SolsticeIdentifier.of(ENTITY, "uuid").get(), (context, str) -> {
            if (!context.hasEntity()) {
                return PlaceholderResult.invalid("No entity!");
            }
            var entity = context.entity();
            return PlaceholderResult.value(entity.getStringUUID());
        });
    }
}
