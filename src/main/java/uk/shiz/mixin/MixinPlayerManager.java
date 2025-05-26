package uk.shiz.mixin;

import net.minecraft.dialog.type.Dialog;
import net.minecraft.network.ClientConnection;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import uk.shiz.TextUtils;
import uk.shiz.command.ChallengeCommand;
import uk.shiz.challenge.Challenge.ChallengeOption;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Mixin(PlayerManager.class)
public abstract class MixinPlayerManager {

    @Inject(method = "onPlayerConnect", at = @At(value = "RETURN"))
    public void afterPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) throws URISyntaxException {
        var challenge = ChallengeCommand.createResponseListener(player, (ch, playerAnswer) -> {

            if (ch.correctResponse.equals(playerAnswer)) {
                ch.solve();
                player.sendMessage(TextUtils.ParseQuickText(
                        String.format("<green>恭喜你，%s，挑战[%s]成功！</green>", player.getName().getString(), ch.challengeId)
                ));
            } else {
                System.out.println(String.format("Player %s failed the challenge[%s] with answer: %s", player.getName().getString(), ch.challengeId, playerAnswer));
            }
            return 0;
        });
        var options = new ArrayList<>(List.of(
                new ChallengeOption("しょうご", "A"),
                new ChallengeOption("しょうごう", "B"),
                new ChallengeOption("そうご", "C"),
                new ChallengeOption("そうごう", "D")
        ));
        Collections.shuffle(options);
        challenge.setChallenge(
                "AAAAB3N",
                TextUtils.ParseQuickText("これからも様々な国との<red><b>相互</b></red>理解を深めていこうと思う。"),
                "C",
                options
        );

        Dialog dialog = uk.shiz.challenge.Dialog.getChallengeDialog(TextUtils.ParseQuickText("N2 真题"), challenge);
        player.openDialog(RegistryEntry.of(dialog));
    }
}

