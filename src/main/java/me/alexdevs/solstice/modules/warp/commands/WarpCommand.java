package me.alexdevs.solstice.modules.warp.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.warp.WarpModule;
import me.alexdevs.solstice.modules.warp.data.WarpServerData;
import me.alexdevs.solstice.Solstice;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class WarpCommand extends ModCommand {
    public WarpCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("warp");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(name))
                .then(argument("name", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            if (!context.getSource().isExecutedByPlayer())
                                return CommandSource.suggestMatching(new String[]{}, builder);

                            var serverDate = Solstice.serverData.getData(WarpServerData.class);
                            return CommandSource.suggestMatching(serverDate.warps.keySet().stream(), builder);
                        })
                        .executes(context -> execute(context, StringArgumentType.getString(context, "name"))));
    }

    private int execute(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        var serverDate = Solstice.serverData.getData(WarpServerData.class);
        var warps = serverDate.warps;
        var playerContext = PlaceholderContext.of(player);

        var locale = Solstice.localeManager.getLocale(WarpModule.ID);

        if (!warps.containsKey(name)) {
            context.getSource().sendFeedback(() -> locale.get(
                    "warpNotFound",
                    playerContext,
                    Map.of(
                            "warp", Text.of(name)
                    )
            ), false);
            return 1;
        }

        context.getSource().sendFeedback(() -> locale.get(
                "teleporting",
                playerContext,
                Map.of(
                        "warp", Text.of(name)
                )
        ), false);

        var warpPosition = warps.get(name);
        warpPosition.teleport(player);

        return 1;
    }
}
