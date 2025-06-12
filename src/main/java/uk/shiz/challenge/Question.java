package uk.shiz.challenge;

import uk.shiz.challenge.Challenge.ChallengeOption;

import java.util.ArrayList;
import java.util.List;

public class Question {
    public final String questionText;
    public final String correctAnswer;
    public final ArrayList<ChallengeOption> options;
    public String analysis;

    public Question(String questionText, String correctAnswer, List<ChallengeOption> options) {
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.options = new ArrayList<>(options);
    }

    public static ChallengeOption Option(String name, String value) {
        return new ChallengeOption(name, value);
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }
}
