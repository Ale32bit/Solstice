package me.alexdevs.solstice.modules.utilities.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.utilities.UtilitiesModule;
import me.alexdevs.solstice.modules.utilities.virtualScreenHandlers.VirtualGrindstoneScreenHandler;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class GrindstoneCommand extends ModCommand<UtilitiesModule> {
    public GrindstoneCommand(UtilitiesModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("grindstone");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require("grindstone", 2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    var screen = new SimpleNamedScreenHandlerFactory(
                            (syncId, inventory, p) ->
                                    new VirtualGrindstoneScreenHandler(syncId, inventory, ScreenHandlerContext.create(player.getWorld(), player.getBlockPos())),
                            Text.translatable("container.grindstone_title"));
                    player.openHandledScreen(screen);
                    player.incrementStat(Stats.INTERACT_WITH_GRINDSTONE);

                    return 1;
                });
    }
}
