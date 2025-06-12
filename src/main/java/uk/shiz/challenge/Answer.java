package uk.shiz.challenge;

public record Answer(String playerAnswer, boolean isCorrect) {

    public static Answer from(String playerAnswer, boolean isCorrect) {
        return new Answer(playerAnswer, isCorrect);
    }
}