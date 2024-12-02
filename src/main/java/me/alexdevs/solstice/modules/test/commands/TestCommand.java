package me.alexdevs.solstice.modules.test.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.IModule;
import me.alexdevs.solstice.api.module.ModuleCommand;
import me.alexdevs.solstice.modules.test.TestPermissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;


public class TestCommand extends ModuleCommand {
    public TestCommand(IModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("test");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return CommandManager.literal(name)
                .requires(TestPermissions.TEST_COMMAND.getRequirement())
                .executes(context -> {
                    context.getSource().sendFeedback(() -> Text.of("OK"), false);
                    return 1;
                });
    }


}
