package me.alexdevs.solstice.modules.placeholders;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import me.alexdevs.solstice.api.module.ModuleBase;
import net.minecraft.resources.ResourceLocation;

public class PlaceholdersModule extends ModuleBase.Toggleable {
    
    public static final String ENTITY = "entity";

    public PlaceholdersModule(ResourceLocation id) {
        super(id);
    }

    @Override
    public void init() {
        //? if >= 1.21.1 {
        /*Placeholders.register(ResourceLocation.fromNamespaceAndPath(ENTITY, "name"), (context, str) -> {*/
        //? } else {
        Placeholders.register(new ResourceLocation(ENTITY, "name"), (context, str) -> {
        //? }
            if (!context.hasEntity()) {
                return PlaceholderResult.invalid("No entity!");
            }
            var entity = context.entity();
            return PlaceholderResult.value(entity.getName());
        });

        //? if >= 1.21.1 {
        /*Placeholders.register(ResourceLocation.fromNamespaceAndPath(ENTITY, "displayname"), (context, str) -> {*/
        //? } else {
        Placeholders.register(new ResourceLocation(ENTITY, "displayname"), (context, str) -> {
        //? }
            if (!context.hasEntity()) {
                return PlaceholderResult.invalid("No entity!");
            }
            var entity = context.entity();
            return PlaceholderResult.value(entity.getDisplayName());
        });

        //? if >= 1.21.1 {
        /*Placeholders.register(ResourceLocation.fromNamespaceAndPath(ENTITY, "uuid"), (context, str) -> {*/
        //? } else {
        Placeholders.register(new ResourceLocation(ENTITY, "uuid"), (context, str) -> {
        //? }
            if (!context.hasEntity()) {
                return PlaceholderResult.invalid("No entity!");
            }
            var entity = context.entity();
            return PlaceholderResult.value(entity.getStringUUID());
        });
    }
}
