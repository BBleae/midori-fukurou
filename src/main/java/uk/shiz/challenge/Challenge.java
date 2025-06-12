package uk.shiz.challenge;

import net.minecraft.text.Text;
import uk.shiz.TextUtils;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.BiFunction;

public class Challenge {
    public String challengeId;
    public final BiFunction<Challenge, String, Integer> callbackFunction;
    public final UUID puuid;
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

    public static class ChallengeOption {
        public final Text name;
        public final String value;

        public ChallengeOption(String name, String value) {
            this.name = TextUtils.ParseQuickText(name);
            this.value = value;
        }
    }
}