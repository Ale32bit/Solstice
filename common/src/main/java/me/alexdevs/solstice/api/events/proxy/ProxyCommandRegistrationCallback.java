package me.alexdevs.solstice.api.events.proxy;

import com.mojang.brigadier.CommandDispatcher;
import me.alexdevs.solstice.api.events.backend.Event;
import me.alexdevs.solstice.api.events.backend.EventFactory;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public interface ProxyCommandRegistrationCallback {
    Event<ProxyCommandRegistrationCallback> EVENT = EventFactory.createArrayBacked(
            ProxyCommandRegistrationCallback.class, callbacks -> (dispatcher, buildContext, selection) -> {
                for (var callback : callbacks) {
                    callback.onRegister(dispatcher, buildContext, selection);
                }
            }
    );

    void onRegister(
            CommandDispatcher<CommandSourceStack> dispatcher,
            CommandBuildContext buildContext,
            Commands.CommandSelection selection
    );
}

