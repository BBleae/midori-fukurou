package uk.shiz.mixin;

import net.minecraft.dialog.DialogButton;
import net.minecraft.dialog.DialogButtonData;
import net.minecraft.dialog.DialogCommonData;
import net.minecraft.dialog.Dialogs;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.dialog.type.DialogListDialog;
import net.minecraft.dialog.type.MultiActionDialog;
import net.minecraft.dialog.type.ServerLinksDialog;
import net.minecraft.network.ClientConnection;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DialogTags;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@Mixin(PlayerManager.class)
public abstract class MixinPlayerManager {

    @Inject(method = "onPlayerConnect", at = @At(value = "RETURN"))
    public void afterPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) throws URISyntaxException {
        var commonData = new DialogCommonData(
                Text.literal("Welcome to NekoCraft!"),
                Optional.empty(),
                false, List.of()
        );
        var btnData = new DialogButtonData(Text.literal("ovo"), 128);
        var btn = new DialogButton(btnData, Optional.of(
                new ClickEvent.OpenUrl(new URI("https://nekocraft.net"))
        ));
        Dialog dialog = new MultiActionDialog(
                commonData,
                List.of(
                        btn
                ),
                Optional.empty(),
                1
        );
        var d = RegistryEntry.of(dialog);
        player.openDialog(d);
        player.sendMessage(
                Text.literal(String.format("Welcome to NekoCraft, %s! Enjoy your stay!", player.getName().getString()))
                        .formatted(net.minecraft.util.Formatting.GREEN)
        );
    }
}

