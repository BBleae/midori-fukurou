package uk.shiz.command;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import uk.shiz.MidoriFukurou;
import uk.shiz.TextUtils;
import uk.shiz.challenge.Challenge;
import uk.shiz.challenge.ChallengeManager;
import uk.shiz.challenge.Question;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static uk.shiz.challenge.Question.Option;

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
                MidoriFukurou.LOGGER.error(String.format("No challenge ID provided, args: %s", Arrays.toString(args)));
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

        commandReg.newCommand("test", (context, text) -> {
            new Thread(() -> {
                HttpResponse<JsonNode> response = Unirest.get("https://midori-api.satori.workers.dev/randomQuiz?maxCount=1")
                        .asJson();

                if (response.getStatus() != 200) {
                    MidoriFukurou.LOGGER.error("Failed to fetch challenge: " + response.getStatusText());
                    return;
                }

                JsonNode body = response.getBody();
                System.out.println("Response body: " + body.getArray().get(0));

                var questionData = body.getArray().getJSONObject(0);
                var title = questionData.getString("title");
                var optionsArray = questionData.getJSONArray("options");
                var rightAnswer = questionData.getString("rightAnswer");
                var analysis = questionData.optString("analysis", "");

                var opts = new ArrayList<Challenge.ChallengeOption>();
                for (int i = 0; i < optionsArray.length(); i++) {
                    var option = optionsArray.getString(i).trim();
                    opts.add(new Challenge.ChallengeOption(option, String.valueOf(i)));
                }

                var q = new Question(
                        title,
                        rightAnswer,
                        opts
                );
                q.setAnalysis(analysis);

                var player = context.getSource().getPlayer();
                ChallengeManager.sendChallengeToPlayer(q, player, false);
            }).start();
            return 1;
        }).registerThis();

        commandReg.newCommand("testmsg", (context, text) -> {
            var player = context.getSource().getPlayer();
            player.sendMessage(TextUtils.ParseQuickText(
                    String.format("MSG: %s", text.getString())
            ));
            return 1;
        }).registerThis();
    }
}
