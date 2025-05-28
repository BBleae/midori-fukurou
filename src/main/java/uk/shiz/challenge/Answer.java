package uk.shiz.challenge;

public class Answer {
    public String playerAnswer;
    public boolean isCorrect;

    public Answer(String playerAnswer, boolean isCorrect) {
        this.playerAnswer = playerAnswer;
        this.isCorrect = isCorrect;
    }

    public static Answer from(String playerAnswer, boolean isCorrect) {
        return new Answer(playerAnswer, isCorrect);
    }
}