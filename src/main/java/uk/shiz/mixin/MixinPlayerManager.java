package uk.shiz.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import uk.shiz.TextUtils;


@Mixin(PlayerManager.class)
public abstract class MixinPlayerManager {

    @Inject(method = "onPlayerConnect", at = @At(value = "RETURN"))
    public void afterPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        player.sendMessage(
                TextUtils.ParseQuickText(
                        String.format("Welcome to the server, %s", player.getDisplayName().getString())
                )
        );
    }
}

