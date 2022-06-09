package com.drakotrogdor.command2velocity;

/*
    Commented out until used
 import com.google.common.io.ByteArrayDataOutput;
 import com.google.common.io.ByteStreams;
*/

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class Command2VelocityFabric implements ModInitializer {
    private static Logger LOGGER;
    public static Logger getLogger() {
        if (LOGGER == null) { LOGGER = LogManager.getLogger("Command2Velocity"); }
        return LOGGER;
    }
    @Override
    public void onInitialize() {
        this.getLogger().info("c2v: Starting onInit()");
        ServerLifecycleEvents.SERVER_STARTED.register((server -> {
            this.getLogger().info("c2v: ServerLifecycleEvents.SERVER_STARTED.register");
            this.registerCommands(server.getCommandManager().getDispatcher(), server.isDedicated());
        }));

        //ConfigUtil config = new ConfigUtil();
        //config.load();
        //

/*
        CommandRegistrationCallback.EVENT.register((CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) -> {
            dispatcher.register(CommandManager.literal("command2velocity")
                .then(CommandManager.literal("server")
                    .executes(context -> {
                        getLogger().info("c2v server");
                        return 1;
                    })
                )
            );
        });
*/
/*
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            getLogger().info("ServerPlayConnectionEvents.JOIN");
        });
*/
    }
    private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        this.getLogger().info("c2v: registerCommands");
        dispatcher.register(CommandManager.literal("command2velocity")
                .then(CommandManager.literal("server")
                        .executes(context -> {
                            getLogger().info("c2v server");
                            return 1;
                        })
                )
        );
    }
}
