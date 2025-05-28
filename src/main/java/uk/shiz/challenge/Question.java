package uk.shiz.challenge;

import uk.shiz.challenge.Challenge.ChallengeOption;

import java.util.ArrayList;
import java.util.List;

import static uk.shiz.challenge.Question.Option;

public class Question {
    public String questionText;
    public String correctAnswer;
    public ArrayList<ChallengeOption> options;
    public String analysis;

    public Question(String questionText, String correctAnswer, List<ChallengeOption> options) {
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.options = new ArrayList<>(options);
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public static ChallengeOption Option(String name, String value) {
        return new ChallengeOption(name, value);
    }
}
