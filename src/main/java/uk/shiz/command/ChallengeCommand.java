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

import static uk.shiz.challenge.ChallengeManager.sendChallengeToPlayer;
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

    private static Question parseQuestionFromJson(JsonNode body) {
        var jsonObj = body.getArray().getJSONObject(0);
        String title = jsonObj.getString("title");
        String rightAnswer = jsonObj.getString("rightAnswer");
        String analysis = jsonObj.optString("analysis", "");
        var optionsArray = jsonObj.getJSONArray("options");

        List<Challenge.ChallengeOption> options = new ArrayList<>();
        for (int i = 0; i < optionsArray.length(); i++) {
            String optionText = optionsArray.getString(i).trim();
            options.add(new Challenge.ChallengeOption(optionText, String.valueOf(i)));
        }

        Question question = new Question(title, rightAnswer, options);
        question.setAnalysis(analysis);
        return question;
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

        commandReg.newCommand("exam", (context, text) -> {
                    var player = context.getSource().getPlayer();
                    new Thread(() -> {
                        HttpResponse<JsonNode> response = Unirest.get("https://midori-api.satori.workers.dev/randomQuiz?maxCount=1")
                                .asJson();
                        if (response.getStatus() != 200) {
                            MidoriFukurou.LOGGER.error("Failed to fetch challenge: " + response.getStatusText());
                            return;
                        }
                        JsonNode body = response.getBody();
                        Question q = parseQuestionFromJson(body);
                        sendChallengeToPlayer(q, player, (answer) -> {
                            if (answer.isCorrect == false) {
                                player.sendMessage(TextUtils.ParseQuickText(
                                        String.format("===========================\n"
                                                + "<yellow>挑战失败</yellow>"
                                        )
                                ));
                            } else {
                                player.sendMessage(TextUtils.ParseQuickText(
                                        String.format("===========================\n" +
                                                        "<green>挑战成功！</green>"
                                        )
                                ));
                            }
                            var opts = q.options.stream()
                                    .map(opt -> {
                                        var currentIdx = Integer.parseInt(opt.value);
                                        if (opt.value.equals(q.correctAnswer)) {
                                            return String.format("<green>(%s) %s</green>",
                                                    currentIdx + 1,
                                                    opt.name.getString());
                                        }
                                        return String.format("<blue>(%s) %s</blue>",
                                                currentIdx + 1,
                                                opt.name.getString());
                                    })
                                    .reduce((a, b) -> a + "\n" + b)
                                    .orElse("");

                            var playerAnswerLine = answer.isCorrect ? "你的答案: <green>%s</green>" : "你的答案: <red>%s</red>\n\n";
                            player.sendMessage(
                                    TextUtils.ParseQuickText(
                                            String.format("<aqua>%s</aqua>\n" +
                                                            "<purple>%s</purple>\n\n" +
                                                            playerAnswerLine +
                                                            "<purple>\n%s</purple>",
                                                    q.questionText,
                                                    opts,
                                                    Integer.parseInt(answer.playerAnswer) + 1,
                                                    q.analysis.replaceAll("\r", "")
                                            )
                                    )
                            );
                            return 0;
                        });
                    }).start();
                    return 1;
                }).

                registerThis();
    }
}
