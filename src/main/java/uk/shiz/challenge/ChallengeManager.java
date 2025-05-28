package uk.shiz.challenge;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import uk.shiz.TextUtils;
import uk.shiz.command.ChallengeCommand;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ChallengeManager {
    public static void sendChallengeToPlayer(
            Question q,
            ServerPlayerEntity player,
            Boolean mustSolve
    ) {

        BlockingQueue<Boolean> queue = new LinkedBlockingQueue<>();
        Thread producer = asyncAnswerWaiterThread(player, queue, q);
        try {
            var result = queue.take();
            producer.join();
            System.out.println("Player answer received: " + result);
            if (result.equals(false)) {
                System.out.println("Challenge cancelled by player or failed.");
                if (mustSolve) {
                    player.sendMessage(TextUtils.ParseQuickText(
                            String.format("<red>挑战失败，请重新尝试！</red>")
                    ));
                    sendChallengeToPlayer(q,player, mustSolve);
                } else {
                    player.sendMessage(TextUtils.ParseQuickText(
                            String.format("<yellow>挑战失败</yellow>")
                    ));
                }
            } else {
                player.sendMessage(TextUtils.ParseQuickText(
                        String.format("<green>恭喜你，%s，挑战成功！</green>", player.getName().getString())
                ));
            }
            player.sendMessage(
                    TextUtils.ParseQuickText(q.analysis)
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static @NotNull Thread asyncAnswerWaiterThread(ServerPlayerEntity player, BlockingQueue<Boolean> queue, Question q) {
        Thread producer = new Thread(() -> {
            var challenge = ChallengeCommand.createResponseListener(player, (ch, playerAnswer) -> {
                Boolean answerCorrect = ch.correctResponse.equals(playerAnswer);
                System.out.println("Player answer: " + playerAnswer + ", correct answer: " + ch.correctResponse);
                try {
                    if (answerCorrect) {
                        ch.solve();
                    }
                    queue.put(answerCorrect);
                } catch (InterruptedException e) {
                    System.err.println("Failed to put player answer in queue: " + e.getMessage());
                }
                return 0;
            });
//            Collections.shuffle(q.options);
            String randChallengeID = "N2_Challenge_" + System.currentTimeMillis();
            challenge.setChallenge(
                    randChallengeID,
                    q.questionText,
                    q.correctAnswer,
                    q.options
            );
            try {
                net.minecraft.dialog.type.Dialog dialog = Dialog.getChallengeDialog("N2 真题", challenge);
                player.openDialog(RegistryEntry.of(dialog));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
        producer.start();
        return producer;
    }
}
