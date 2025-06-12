package uk.shiz;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.shiz.command.CommandRegistry;

public class MidoriFukurou implements ModInitializer {
    public static final String MOD_ID = "midorifukurou";
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static MinecraftServer server;

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
//		Registries

        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> {
                    // Register commands here
                    // Example: CommandRegistry.register(dispatcher);
                    LOGGER.info("Registering commands for {}", MOD_ID);
                    CommandRegistry.register(dispatcher, registryAccess);
                }
        );
        ServerLifecycleEvents.SERVER_STARTING.register(
                (MinecraftServer server) -> MidoriFukurou.server = server
        );
    }
}
