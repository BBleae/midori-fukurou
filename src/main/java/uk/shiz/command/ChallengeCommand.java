package uk.shiz.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ChallengeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess)  {
        dispatcher.register(
            CommandManager.literal("challenge")
                .then(CommandManager.argument("response", TextArgumentType.text(commandRegistryAccess))
                    .executes(context -> {
                        System.out.println("Challenge command executed");
                        Text text = TextArgumentType.getTextArgument(context, "response");
                        System.out.println("Response: " + text.getString());
//                        try {
//                            ServerCommandSource source = context.getSource();
//                            ServerPlayerEntity player = source.getPlayer();
//                            var response = MessageArgumentType.getMessage(context, "response").getString();
//                            System.out.println(String.format("Player %s challenged with response: %s", player.getName().getString(), response));
//                            var splitResponse = response.split(" ", 2);
//                            if (splitResponse.length < 2) {
//                                return 0;
//                            }
//                            var question = splitResponse[0];
//                            var answer = splitResponse[1];
//                            System.out.println(String.format("Player %s challenged with question: %s, answer: %s", player.getName().getString(), question, answer));
//                        } catch (Exception e) {
//                            System.err.println("Error executing challenge command: " + e.getMessage());
//                        }
                        return 1;
                    }))
        );
    }
}
