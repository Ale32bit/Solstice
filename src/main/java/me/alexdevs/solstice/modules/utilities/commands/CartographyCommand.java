package me.alexdevs.solstice.modules.utilities.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.utilities.UtilitiesModule;
import net.minecraft.screen.CartographyTableScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class CartographyCommand extends ModCommand<UtilitiesModule> {
    public CartographyCommand(UtilitiesModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("cartography");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    var screen = new SimpleNamedScreenHandlerFactory(
                            (syncId, inventory, p) ->
                                    new CartographyTableScreenHandler(syncId, inventory, ScreenHandlerContext.EMPTY),
                            Text.translatable("container.cartography_table"));
                    player.openHandledScreen(screen);
                    player.incrementStat(Stats.INTERACT_WITH_CARTOGRAPHY_TABLE);

                    return 1;
                });
    }
}
