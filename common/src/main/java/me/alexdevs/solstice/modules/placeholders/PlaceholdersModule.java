package me.alexdevs.solstice.modules.placeholders;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import me.alexdevs.solstice.api.module.ModuleBase;
import net.minecraft.resources.ResourceLocation;

public class PlaceholdersModule extends ModuleBase.Toggleable {
    public static final String ID = "placeholders";
    public static final String ENTITY = "entity";

    public PlaceholdersModule() {
        super(ID);
    }

    @Override
    public void init() {
        Placeholders.register(ResourceLocation.fromNamespaceAndPath(ENTITY, "name"), (context, str) -> {
            if(!context.hasEntity()) {
                return PlaceholderResult.invalid("No entity!");
            }
            var entity = context.entity();
            return PlaceholderResult.value(entity.getName());
        });

        Placeholders.register(ResourceLocation.fromNamespaceAndPath(ENTITY, "displayname"), (context, str) -> {
            if(!context.hasEntity()) {
                return PlaceholderResult.invalid("No entity!");
            }
            var entity = context.entity();
            return PlaceholderResult.value(entity.getDisplayName());
        });

        Placeholders.register(ResourceLocation.fromNamespaceAndPath(ENTITY, "uuid"), (context, str) -> {
            if(!context.hasEntity()) {
                return PlaceholderResult.invalid("No entity!");
            }
            var entity = context.entity();
            return PlaceholderResult.value(entity.getStringUUID());
        });
    }
}
