package me.alexdevs.solstice.api.module;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

public abstract class ModuleCommand {
    protected final IModule module;
    public ModuleCommand(IModule module) {
        this.module = module;
    }

    public abstract List<String> getNames();

    public abstract LiteralArgumentBuilder<ServerCommandSource> command(String name);
}
