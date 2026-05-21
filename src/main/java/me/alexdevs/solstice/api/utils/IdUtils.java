package me.alexdevs.solstice.api.utils;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

public class IdUtils {


    public static ArgumentType<?> idArgument() {
        //? < 1.21.11
        return net.minecraft.commands.arguments.ResourceLocationArgument.id();
        //? >= 1.21.11
        //return net.minecraft.commands.arguments.IdentifierArgument.id();
    }

    public static Object getIdArgument(CommandContext<CommandSourceStack> context, String name) {
        //? < 1.21.11
        return net.minecraft.commands.arguments.ResourceLocationArgument.getId(context, name);
        //? >= 1.21.11
        //return net.minecraft.commands.arguments.IdentifierArgument.getId(context, name);
    }
}
