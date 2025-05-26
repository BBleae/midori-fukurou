package uk.shiz.command;

import net.minecraft.server.network.ServerPlayerEntity;
import uk.shiz.MidoriFukurou;
import uk.shiz.challenge.Challenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ChallengeCommand {
    private static ArrayList<Challenge> challenges = new ArrayList<>();

    public static Challenge createResponseListener(
            ServerPlayerEntity p,
            BiFunction<Challenge, String, Integer> callbackFunction
    ) {
        var challenge = new Challenge(p.getUuid(), callbackFunction);
        challenges.add(challenge);
        return challenge;
    }

    public static void register(CommandRegister commandReg) {
        commandReg.newCommand("challenge", (context, text) -> {
            var args = Arrays.stream(text.getString().split(" ")).filter(s -> !s.isEmpty()).toArray(String[]::new);
            if (args.length < 2) {
                MidoriFukurou.LOGGER.error("No challenge ID provided.");
                return 0;
            }
            var puuid = context.getSource().getPlayer().getUuid();
            var challengeId = args[0];

            for (Challenge challenge : challenges) {
                if (challenge.puuid.equals(puuid) && challenge.challengeId.equals(challengeId)) {
                    challenge.callbackFunction.apply(challenge, args[1]);
                    return 1;
                }
            }
            MidoriFukurou.LOGGER.error("No challenge found for player " + context.getSource().getPlayer().getName().getString() + " with ID " + challengeId);
            return 1;
        }).registerThis();
    }
}
