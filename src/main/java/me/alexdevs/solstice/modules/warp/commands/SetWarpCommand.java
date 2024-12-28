package me.alexdevs.solstice.modules.warp.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerPosition;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.warp.WarpModule;
import me.alexdevs.solstice.modules.warp.data.WarpServerData;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SetWarpCommand extends ModCommand<WarpModule> {
    public SetWarpCommand(WarpModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("setwarp");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require("set", 2))
                .then(argument("name", StringArgumentType.word())
                        .executes(context -> execute(context,
                                StringArgumentType.getString(context, "name"))));
    }

    private int execute(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        var serverData = Solstice.serverData.getData(WarpServerData.class);

        var warps = serverData.warps;

        var warpPosition = new ServerPosition(player);
        warps.put(name, warpPosition);

        context.getSource().sendFeedback(() -> Text.literal("New warp ")
                .append(Text.literal(name).formatted(Formatting.GOLD))
                .append(" set!")
                .formatted(Formatting.GREEN), false);

        return 1;
    }
}
