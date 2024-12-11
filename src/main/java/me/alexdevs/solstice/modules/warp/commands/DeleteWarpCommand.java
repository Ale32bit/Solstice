package me.alexdevs.solstice.modules.warp.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.Solstice;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.warp.data.WarpServerData;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DeleteWarpCommand extends ModCommand {
    public DeleteWarpCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("delwarp");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(2))
                .then(argument("name", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            if (!context.getSource().isExecutedByPlayer())
                                return CommandSource.suggestMatching(new String[]{}, builder);

                            var serverData = Solstice.serverData.getData(WarpServerData.class);
                            return CommandSource.suggestMatching(serverData.warps.keySet().stream(), builder);
                        })
                        .executes(context -> execute(context, StringArgumentType.getString(context, "name"))));
    }

    private int execute(CommandContext<ServerCommandSource> context, String name) {
        var serverData = Solstice.serverData.getData(WarpServerData.class);
        var warps = serverData.warps;

        if (!warps.containsKey(name)) {
            context.getSource().sendFeedback(() -> Text.literal("The warp ")
                    .append(Text.literal(name).formatted(Formatting.GOLD))
                    .append(" does not exist!")
                    .formatted(Formatting.RED), false);
            return 1;
        }

        warps.remove(name);

        context.getSource().sendFeedback(() -> Text
                .literal("Warp ")
                .append(Text.literal(name).formatted(Formatting.GOLD))
                .append(" deleted!")
                .formatted(Formatting.GREEN), false);

        return 1;
    }
}
