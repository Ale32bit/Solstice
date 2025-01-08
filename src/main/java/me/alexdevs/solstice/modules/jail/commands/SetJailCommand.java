package me.alexdevs.solstice.modules.jail.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.ServerPosition;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.jail.JailModule;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

public class SetJailCommand extends ModCommand<JailModule> {
    public SetJailCommand(JailModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("setjail");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return CommandManager.literal(name)
                .requires(require("set", 3))
                .then(CommandManager.argument("name", StringArgumentType.word())
                        .executes(context -> {
                            var player = context.getSource().getPlayerOrThrow();
                            var jailName = StringArgumentType.getString(context, "name");

                            var position = new ServerPosition(player);

                            var jails = module.getJails();
                            if(jails.containsKey(jailName)) {
                                return 0;
                            }

                            jails.put(jailName, position);

                            return 1;
                        })
                );
    }
}
