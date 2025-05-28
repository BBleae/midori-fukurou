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
        player.sendMessage(
                TextUtils.ParseQuickText(
                        String.format("Welcome to the server, %s", player.getDisplayName().getString())
                )
        );
    }
}

