package me.alexdevs.solstice.modules.warp.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.warp.WarpModule;
import me.alexdevs.solstice.modules.warp.data.WarpServerData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class SetWarpCommand extends ModCommand<WarpModule> {
    public SetWarpCommand(WarpModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("setwarp");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require("set", 2))
                .then(argument("name", StringArgumentType.word())
                        .executes(context -> execute(context,
                                StringArgumentType.getString(context, "name"))));
    }

    private int execute(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        var serverData = Solstice.serverData.getData(WarpServerData.class);

        var warps = serverData.warps;

        var warpPosition = new ServerLocation(player);
        warps.put(name, warpPosition);

        context.getSource().sendSuccess(() -> module.locale().get(
                "created",
                Map.of(
                        "warp", Component.nullToEmpty(name)
                )
        ), true);

        return 1;
    }
}
