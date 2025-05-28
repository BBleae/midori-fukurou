package uk.shiz.challenge;

import net.minecraft.text.Text;
import uk.shiz.TextUtils;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Challenge {
    public String challengeId;
    public BiFunction<Challenge, String, Integer> callbackFunction;
    public UUID puuid;
    public String correctResponse;
    public ArrayList<ChallengeOption> responses = new ArrayList<>();
    public Text challengeText;

    private Boolean solved = false;

    public Challenge(
            UUID puuid,
            BiFunction<Challenge, String, Integer> callbackFunction
    ) {
        this.callbackFunction = callbackFunction;
        this.puuid = puuid;
    }

    public void solve() {
        if (this.solved) {
            return;
        }
        this.solved = true;
    }

    public static class ChallengeOption {
        public Text name;
        public String value;

        public ChallengeOption(String name, String value) {
            this.name = TextUtils.ParseQuickText(name);
            this.value = value;
        }
    }

    public void setChallenge(
            String challengeId,
            String challengeText,
            String correctResponseValue,
            ArrayList<ChallengeOption> responses) {
        this.challengeId = challengeId;
        this.challengeText = TextUtils.ParseQuickText(challengeText);
        this.correctResponse = correctResponseValue;
        this.responses = responses;
    }
}