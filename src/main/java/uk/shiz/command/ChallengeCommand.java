package uk.shiz.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ChallengeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            CommandManager.literal("challenge")
                .then(CommandManager.argument("response", MessageArgumentType.message())
                    .executes(context -> {
                        System.out.println("Challenge command executed");
                        try {
                            ServerCommandSource source = context.getSource();
                            ServerPlayerEntity player = source.getPlayer();
                            var response = MessageArgumentType.getMessage(context, "response").getString();
                            var splitResponse = response.split(" ", 2);
                            if (splitResponse.length < 2) {
                                return 0;
                            }
                            var question = splitResponse[0];
                            var answer = splitResponse[1];
                            System.out.println(String.format("Player %s challenged with question: %s, answer: %s", player.getName().getString(), question, answer));
                        } catch (Exception e) {
                            System.err.println("Error executing challenge command: " + e.getMessage());
                        }
                        return 1;
                    }))
        );
    }
}
