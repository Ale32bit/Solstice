package me.alexdevs.solstice.modules.test;

import me.alexdevs.solstice.api.module.IModule;
import me.alexdevs.solstice.api.module.ModuleCommand;
import me.alexdevs.solstice.core.ServiceProvider;
import me.alexdevs.solstice.modules.test.commands.TestCommand;

import java.util.Collection;
import java.util.List;

public class TestModule implements IModule {
    public static final String ID = "test";

    @Override
    public void initialize(ServiceProvider serviceProvider) {

    }

    @Override
    public Collection<Class<? extends ModuleCommand>> getCommands() {
        return List.of(TestCommand.class);
    }
}
