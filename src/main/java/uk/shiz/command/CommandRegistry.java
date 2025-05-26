package uk.shiz.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;

public class CommandRegistry {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        var commandReg = new CommandRegister(commandRegistryAccess, dispatcher);
        ChallengeCommand.register(commandReg);
    }
}
