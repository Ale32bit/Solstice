package me.alexdevs.solstice.modules;

import me.alexdevs.solstice.api.events.ModuleRegistrationCallback;
import me.alexdevs.solstice.api.module.ModuleContainer;
import me.alexdevs.solstice.modules.test.TestModule;

import java.util.Collection;
import java.util.List;

public final class ModuleProvider {
    public static void initialize() {
        ModuleRegistrationCallback.EVENT.register(ModuleProvider::setup);
    }

    private static Collection<ModuleContainer> setup() {
        return List.of(
                ModuleContainer.of(TestModule.ID, "Test", new TestModule())
        );
    }
}
