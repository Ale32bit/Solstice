package me.alexdevs.solstice.modules.skull.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.skull.SkullModule;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SkullCommand extends ModCommand<SkullModule> {
    public SkullCommand(SkullModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("skull");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(2))
                .executes(context -> execute(context, context.getSource().getPlayerOrThrow().getGameProfile()))
                .then(argument("uuid", UuidArgumentType.uuid())
                        .executes(context -> execute(context, UuidArgumentType.getUuid(context, "uuid"))))
                .then(argument("name", StringArgumentType.word())
                        .executes(context -> execute(context, StringArgumentType.getString(context, "name"))));
    }

    private int execute(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        var skull = module.createSkull(name);

        player.getInventory().insertStack(skull);
        return 1;
    }

    private int execute(CommandContext<ServerCommandSource> context, UUID uuid) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        var skull = module.createSkull(uuid);

        player.getInventory().insertStack(skull);
        return 1;
    }

    private int execute(CommandContext<ServerCommandSource> context, GameProfile profile) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        var skull = module.createSkull(profile);

        player.getInventory().insertStack(skull);
        return 1;
    }
}
