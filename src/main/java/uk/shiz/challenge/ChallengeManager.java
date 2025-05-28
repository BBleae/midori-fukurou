package uk.shiz.challenge;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import uk.shiz.TextUtils;
import uk.shiz.command.ChallengeCommand;

import java.net.URISyntaxException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ChallengeManager {
    public static void sendChallengeToPlayer(
            Question q,
            ServerPlayerEntity player,
            Function<Answer, Integer> callbackFunction
    ) {

        BlockingQueue<Answer> queue = new LinkedBlockingQueue<>();
        Thread producer = asyncAnswerWaiterThread(player, queue, q);
        try {
            var result = queue.take();
            producer.join();
            callbackFunction.apply(result);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static @NotNull Thread asyncAnswerWaiterThread(
            ServerPlayerEntity player,
            BlockingQueue<Answer> queue,
            Question q
    ) {
        Thread producer = new Thread(() -> {
            var challenge = ChallengeCommand.createResponseListener(player, (ch, playerAnswer) -> {
                Boolean answerCorrect = ch.correctResponse.equals(playerAnswer);
                try {
                    if (answerCorrect) {
                        ch.solve();
                    }
                    queue.put(Answer.from(playerAnswer, answerCorrect));
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
