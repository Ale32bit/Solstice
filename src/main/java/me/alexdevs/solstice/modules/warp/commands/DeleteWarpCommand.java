package me.alexdevs.solstice.modules.warp.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.warp.WarpModule;
import me.alexdevs.solstice.modules.warp.data.WarpServerData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class DeleteWarpCommand extends ModCommand<WarpModule> {
    public DeleteWarpCommand(WarpModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("delwarp");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require("set", 2))
                .then(argument("name", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            if (!context.getSource().isPlayer())
                                return SharedSuggestionProvider.suggest(new String[]{}, builder);

                            var serverData = Solstice.serverData.getData(WarpServerData.class);
                            return SharedSuggestionProvider.suggest(serverData.warps.keySet().stream(), builder);
                        })
                        .executes(context -> execute(context, StringArgumentType.getString(context, "name"))));
    }

    private int execute(CommandContext<CommandSourceStack> context, String name) {
        var serverData = Solstice.serverData.getData(WarpServerData.class);
        var warps = serverData.warps;

        if (!warps.containsKey(name)) {
            context.getSource().sendSuccess(() -> module.locale().get(
                    "warpNotFound",
                    Map.of(
                            "warp", Component.nullToEmpty(name)
                    )
            ), true);
            return 0;
        }

        warps.remove(name);

        context.getSource().sendSuccess(() -> module.locale().get(
                "deleted",
                Map.of(
                        "warp", Component.nullToEmpty(name)
                )
        ), true);

        return 1;
    }
}
