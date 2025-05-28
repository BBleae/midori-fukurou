package uk.shiz.command;

import com.mojang.serialization.MapCodec;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import net.minecraft.*;
import net.minecraft.dialog.DialogButtonData;
import net.minecraft.dialog.DialogCommonData;
import net.minecraft.dialog.Dialogs;
import net.minecraft.dialog.type.ColumnsDialog;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.dialog.type.MultiActionDialog;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import uk.shiz.MidoriFukurou;
import uk.shiz.TextUtils;
import uk.shiz.challenge.Challenge;
import uk.shiz.challenge.ChallengeManager;
import uk.shiz.challenge.Question;

import java.net.URISyntaxException;
import java.util.*;
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
        var arr = body.getArray();
        if (arr.length() == 0) {
            MidoriFukurou.LOGGER.error("No questions found in the response.");
            return null;
        }
        var jsonObj = arr.getJSONObject(0);
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

        commandReg.newCommand("examtest", (context, text) -> {
            var player = context.getSource().getPlayer();
            try {
                var dialogData = new DialogCommonData(
                        TextUtils.ParseQuickText(
                                "<green>" +
                                        "<hover show_text 'ABCD'>1919</hover>" +
                                        "</green>"
                        ), Optional.of(Text.literal("DEF")), true, true, class_11520.CLOSE, List.of(), List.of()
                );
                var dialog = new MultiActionDialog(
                        dialogData,
                        List.of(
                                new class_11519(
                                        new DialogButtonData(
                                                TextUtils.ParseQuickText("<green>" +
                                                        "开始挑战" +
                                                        "</green>"),
                                                Optional.of(TextUtils.ParseQuickText("<green>开始挑战</green>")),
                                                128
                                        ),
                                        Optional.of(
                                                new class_11525(
                                                        new ClickEvent.RunCommand("list")
                                                )
                                        )
                                )
                        ),
                        Optional.of(new class_11519(
                                new DialogButtonData(
                                        TextUtils.ParseQuickText("<red>关闭</red>"),
                                        Optional.of(Text.literal("???")),
                                        128
                                ),
                                Optional.of(new class_11525(
                                        new ClickEvent.RunCommand("list")
                                ))
                        )),
                        2
                );
                player.openDialog(RegistryEntry.of(dialog));
            } catch (Exception e) {
                System.err.println("Failed to open dialog: " + e.getMessage());
            }
            return 1;
        }).registerThis();

        commandReg.newCommand("exam", (context, text) -> {
                    var level = text.getString().trim().toLowerCase();
                    var player = context.getSource().getPlayer();
                    new Thread(() -> {
                        HttpResponse<JsonNode> response = Unirest.get("https://midori-api.satori.workers.dev/randomQuiz?maxCount=1&prefix=japanese/jlpt/" + level)
                                .asJson();
                        if (response.getStatus() != 200) {
                            MidoriFukurou.LOGGER.error("Failed to fetch challenge: " + response.getStatusText());
                            return;
                        }
                        JsonNode body = response.getBody();
                        Question q = parseQuestionFromJson(body);
                        if (q == null) {
                            player.sendMessage(TextUtils.ParseQuickText(
                                    String.format("<red>无法获取题目，请稍后再试。</red>")
                            ));
                            return;
                        }
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
